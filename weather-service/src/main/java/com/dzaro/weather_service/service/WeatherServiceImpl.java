package com.dzaro.weather_service.service;

import com.dzaro.weather_service.entity.WeatherRequestHistoryEntity;
import com.dzaro.weather_service.model.DumpAcceptedDto;
import com.dzaro.weather_service.model.HistoryEntry;
import com.dzaro.weather_service.model.WeatherDto;
import com.dzaro.weather_service.repository.WeatherHistoryRequestRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class WeatherServiceImpl {

    private final WeatherHistoryRequestRepository historyRequestRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${adapter.base-url}")
    private String adapterBaseUrl;

    @Value("${dump.inbox.dir}")
    private String dumpFolder;

    public WeatherDto getWeather(String city) {
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City cannot be null or empty");
        }
        try {
            String url = adapterBaseUrl + "/adapter/weather?city=" + city;
            ResponseEntity<WeatherDto> response = restTemplate.getForEntity(url, WeatherDto.class);//todo response to dto
            
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
                .collect(toList());
    }

    HistoryEntry toDto(WeatherRequestHistoryEntity e) {
        HistoryEntry dto = new HistoryEntry();
        dto.setCity(e.getCity());
        dto.setQueryDate(e.getQueryDate());
        dto.setWeatherResponse(e.getWeatherResponseJson());
        return dto;
    }

    public DumpAcceptedDto requestDataDump() {
        try {
            List<HistoryEntry> payload = historyRequestRepository.findAllByOrderByQueryDateDesc()
                    .stream()
                    .map(this::toDto)
                    .toList();

            String requestId = UUID.randomUUID().toString();
            Path dir = Paths.get(dumpFolder);
            Files.createDirectories(dir);
            String filename = "history-" + requestId + ".json";
            Path file = dir.resolve(filename);

            try (var writer = Files.newBufferedWriter(
                    file, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW)) {
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, payload);
            }

            return new DumpAcceptedDto().requestId(requestId);
        } catch (Exception e) {
            throw new RuntimeException("Dump failed: " + e.getMessage(), e);
        }
    }
}
