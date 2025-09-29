package com.dzaro.weather_service.service;

import com.dzaro.weather_service.entity.HistoryEntryEntity;
import com.dzaro.weather_service.model.DumpAcceptedDto;
import com.dzaro.weather_service.model.HistoryEntry;
import com.dzaro.weather_service.model.WeatherDto;
import com.dzaro.weather_service.repository.WeatherRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WeatherServiceImpl implements WeatherService {

    private final WeatherRepository repository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${adapter.base-url}")
    private String adapterBaseUrl;

    @Value("${dump.folder:}")
    private String dumpFolder;

    @Autowired
    public WeatherServiceImpl(WeatherRepository repository, RestTemplate restTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public WeatherDto getWeather(String city) {
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City cannot be null or empty");
        }
        
        try {
            String url = adapterBaseUrl + "/adapter/weather?city=" + city;
            ResponseEntity<WeatherDto> response = restTemplate.getForEntity(url, WeatherDto.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                WeatherDto weatherDto = response.getBody();
                
                HistoryEntryEntity entity = new HistoryEntryEntity();
                entity.setCity(city);
                entity.setQueryDate(OffsetDateTime.now());
                entity.setWeatherResponseJson(objectMapper.writeValueAsString(weatherDto));
                repository.save(entity);
                
                return weatherDto;
            } else {
                throw new RuntimeException("Failed to fetch weather from adapter service");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching weather data: " + e.getMessage(), e);
        }
    }

    @Override
    public List<HistoryEntry> getHistory(String city, LocalDate from, LocalDate to) {
        List<HistoryEntryEntity> entities;
        if (city != null || from != null || to != null) {
            entities = repository.findByCityAndDateRange(city, from, to);
        } else {
            entities = repository.findAll();
        }
        
        return entities.stream().map(e -> {
            HistoryEntry dto = new HistoryEntry();
            dto.setId(e.getId());
            dto.setCity(e.getCity());
            dto.setQueryDate(e.getQueryDate());
            dto.setWeatherResponse(objectMapper.convertValue(
                    e.getWeatherResponseJson(), Object.class));
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public DumpAcceptedDto requestDataDump() {
        try {
            String requestId = UUID.randomUUID().toString();
            if (dumpFolder == null || dumpFolder.isBlank()) {
                return new DumpAcceptedDto().requestId(requestId).message("Dump folder not configured");
            }
            Path folder = Path.of(dumpFolder);
            Files.createDirectories(folder);
            Path out = folder.resolve("dump-" + requestId + ".txt");
            Files.writeString(out, "dump accepted: " + requestId);
            return new DumpAcceptedDto().requestId(requestId).message("Dump file created");
        } catch (Exception e) {
            return new DumpAcceptedDto().requestId(null).message("Dump failed: " + e.getMessage());
        }
    }

}
