package com.dzaro.weather_service.service;

import com.dzaro.weather_service.entity.IdempotencyRecordEntity;
import com.dzaro.weather_service.entity.IdempotencyStatus;
import com.dzaro.weather_service.entity.WeatherRequestHistoryEntity;
import com.dzaro.weather_service.model.DumpAcceptedDto;
import com.dzaro.weather_service.model.HistoryEntry;
import com.dzaro.weather_service.model.WeatherDto;
import com.dzaro.weather_service.repository.IdempotencyRecordRepository;
import com.dzaro.weather_service.repository.WeatherHistoryRequestRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WeatherServiceImpl {

    private final WeatherHistoryRequestRepository historyRequestRepository;
    private final IdempotencyRecordRepository idempotencyRecordRepository;
    private final AdapterClient adapterClient;
    private final ObjectMapper objectMapper;

    @Value("${dump.inbox.dir}")
    private String dumpFolder;

    public WeatherDto getWeather(String city) {
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City cannot be null or empty");
        }
        try {
            ResponseEntity<WeatherDto> response = adapterClient.fetchWeather(city);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                WeatherDto weatherDto = response.getBody();
                WeatherRequestHistoryEntity weatherRequestHistory = new WeatherRequestHistoryEntity();
                weatherRequestHistory.setCity(city);
                weatherRequestHistory.setQueryDate(OffsetDateTime.now());
                weatherRequestHistory.setWeatherResponseJson(objectMapper.valueToTree(weatherDto));
                historyRequestRepository.save(weatherRequestHistory);
                return weatherDto;
            } else {
                throw new RuntimeException("Failed to fetch weather from adapter service");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching weather data: " + e.getMessage(), e);
        }
    }

    public List<HistoryEntry> getHistory(String city, LocalDate from, LocalDate to) {
        OffsetDateTime min = OffsetDateTime.of(1970,1,1,0,0,0,0,
                ZoneId.systemDefault().getRules().getOffset(Instant.EPOCH));
        OffsetDateTime max = OffsetDateTime.now().plusYears(100);

        OffsetDateTime fromTs = (from != null)
                ? from.atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime()
                : min;

        OffsetDateTime toTs = (to != null)
                ? to.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toOffsetDateTime()
                : max;

        var historyEntries = historyRequestRepository.findByCityAndDateRange(city, fromTs, toTs);
        return historyEntries
                .stream()
                .map(this::toDto)
                .toList();
    }

    HistoryEntry toDto(WeatherRequestHistoryEntity e) {
        HistoryEntry dto = new HistoryEntry();
        dto.setCity(e.getCity());
        dto.setQueryDate(e.getQueryDate());
        dto.setWeatherResponse(e.getWeatherResponseJson());
        return dto;
    }

    public DumpAcceptedDto requestDataDump(String idempotencyKey) {
        try {
            String normalizedIdempotencyKey = normalizeIdempotencyKey(idempotencyKey);
            if (normalizedIdempotencyKey == null) {
                String requestId = createDumpFile();
                return new DumpAcceptedDto()
                        .requestId(requestId)
                        .message("Dump request accepted without idempotency key.");
            }

            return idempotencyRecordRepository.findByIdempotencyKey(normalizedIdempotencyKey)
                    .map(this::toDumpAcceptedDto)
                    .orElseGet(() -> createIdempotentDump(normalizedIdempotencyKey));
        } catch (Exception e) {
            throw new RuntimeException("Dump failed: " + e.getMessage(), e);
        }
    }

    private DumpAcceptedDto createIdempotentDump(String idempotencyKey) {
        String requestId = UUID.randomUUID().toString();

        IdempotencyRecordEntity record = new IdempotencyRecordEntity();
        record.setIdempotencyKey(idempotencyKey);
        record.setRequestId(requestId);
        record.setStatus(IdempotencyStatus.IN_PROGRESS);
        record.setCreatedAt(OffsetDateTime.now());

        try {
            idempotencyRecordRepository.saveAndFlush(record);
        } catch (DataIntegrityViolationException ex) {
            return idempotencyRecordRepository.findByIdempotencyKey(idempotencyKey)
                    .map(this::toDumpAcceptedDto)
                    .orElseThrow(() -> new RuntimeException("Idempotency record already exists but could not be loaded", ex));
        }

        try {
            createDumpFile(requestId);
            record.setStatus(IdempotencyStatus.COMPLETED);
            idempotencyRecordRepository.save(record);
            return new DumpAcceptedDto()
                    .requestId(requestId)
                    .message("Dump request accepted.");
        } catch (Exception ex) {
            idempotencyRecordRepository.delete(record);
            throw new RuntimeException("Dump failed: " + ex.getMessage(), ex);
        }
    }

    private DumpAcceptedDto toDumpAcceptedDto(IdempotencyRecordEntity record) {
        String message = record.getStatus() == IdempotencyStatus.COMPLETED
                ? "Request with the same Idempotency-Key was already processed. Returning the existing requestId."
                : "Request with the same Idempotency-Key is already being processed. Returning the current requestId.";

        return new DumpAcceptedDto()
                .requestId(record.getRequestId())
                .message(message);
    }

    private String createDumpFile() throws Exception {
        return createDumpFile(UUID.randomUUID().toString());
    }

    private String createDumpFile(String requestId) throws Exception {
        List<HistoryEntry> payload = historyRequestRepository.findAllByOrderByQueryDateDesc()
                .stream()
                .map(this::toDto)
                .toList();

        Path dir = Paths.get(dumpFolder);
        Files.createDirectories(dir);
        String filename = "history-" + requestId + ".json";
        Path file = dir.resolve(filename);
        try (var writer = Files.newBufferedWriter(
                file, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW)) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, payload);
        }
        return requestId;
    }

    private String normalizeIdempotencyKey(String idempotencyKey) {
        if (idempotencyKey == null) {
            return null;
        }

        String normalized = idempotencyKey.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
