package com.dzaro.weather_adapter.service;

import com.dzaro.weather_adapter.model.WeatherDto;
import org.springframework.stereotype.Service;

@Service
public class AdapterServiceImpl implements AdapterService {

    @Override
    public WeatherDto getWeather(String city) {
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City cannot be null or empty");
        }
        
        // Mock weather data - in a real implementation, this would call an external weather API
        WeatherDto dto = new WeatherDto()
                .city(city)
                .temperature(25.5)
                .description("Sunny");
        return dto;
    }

}
