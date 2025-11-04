package com.dzaro.weather_adapter.delegate;


import com.dzaro.weather_adapter.api.AdapterApiDelegate;
import com.dzaro.weather_adapter.model.WeatherDto;
import com.dzaro.weather_adapter.service.AdapterService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientResponseException;

@RestController
@RequiredArgsConstructor
public class AdapterDelegateImpl implements AdapterApiDelegate {

    private final AdapterService adapterService;

    @Override
    public ResponseEntity<WeatherDto> adapterWeatherGet(String city) {
        if (city == null || city.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            WeatherDto weatherDto = adapterService.getWeather(city);
            return ResponseEntity.ok(weatherDto);
        } catch (RestClientResponseException ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}
