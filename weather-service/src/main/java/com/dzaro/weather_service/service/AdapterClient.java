package com.dzaro.weather_service.service;

import com.dzaro.weather_service.model.WeatherDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class AdapterClient {

    private final RestTemplate restTemplate;

    @Value("${adapter.base-url}")
    private String adapterBaseUrl;

    @Retryable(
            retryFor = {ResourceAccessException.class, HttpServerErrorException.class},
            maxAttemptsExpression = "${adapter.retry.max-attempts:2}",
            backoff = @Backoff(delayExpression = "${adapter.retry.delay-ms:200}")
    )
    public ResponseEntity<WeatherDto> fetchWeather(String city) {
        String url = adapterBaseUrl + "/adapter/weather?city=" + city;
        return restTemplate.getForEntity(url, WeatherDto.class);
    }

    @Recover
    public ResponseEntity<WeatherDto> recover(Exception ex, String city) {
        throw new RuntimeException("Adapter service unavailable for city: " + city, ex);
    }
}