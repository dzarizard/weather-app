package com.dzaro.weather_service.service;

import com.dzaro.weather_service.entity.WeatherHistoryEntity;
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

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class WeatherServiceImpl implements WeatherService {

    private final WeatherRepository repository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${adapter.base-url}")
    private String adapterBaseUrl;

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
            ResponseEntity<WeatherDto> response = restTemplate.getForEntity(url, WeatherDto.class);//todo response to dto
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                WeatherDto weatherDto = response.getBody();
                WeatherHistoryEntity entity = new WeatherHistoryEntity();
                entity.setCity(city);//todo set correct fields from dto
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
    public List<WeatherHistoryEntity> getHistory(String city, LocalDate from, LocalDate to) {
        List<WeatherHistoryEntity> entities;
        if (city != null || from != null || to != null) {
            entities = repository.findByCityAndDateRange(city, from, to);
        } else {
            entities = repository.findAll();
        }
        
        return entities;
    }

    @Override
    public DumpAcceptedDto requestDataDump() {
        return new DumpAcceptedDto("Not implemented yet");
    }

}
