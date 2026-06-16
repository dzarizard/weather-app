package com.dzaro.weather_adapter.service;

import com.dzaro.weather_adapter.model.WeatherDto;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.function.Supplier;

@Service
public class AdapterServiceImpl implements AdapterService {

    private final RestTemplate restTemplate;
    private final CircuitBreaker weatherApiCircuitBreaker;
    private final Retry weatherApiRetry;
    private final String baseUrl;
    private final String apiKey;

    public AdapterServiceImpl(
            RestTemplate restTemplate,
            CircuitBreaker weatherApiCircuitBreaker,
            Retry weatherApiRetry,
            @Value("${adapter.external.base-url}") String baseUrl,
            @Value("${adapter.external.api-key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.weatherApiCircuitBreaker = weatherApiCircuitBreaker;
        this.weatherApiRetry = weatherApiRetry;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    @Override
    public WeatherDto getWeather(String city) {
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("city must not be blank");
        }

        URI uri = UriComponentsBuilder
                .fromUriString(baseUrl)
                .path("/data/2.5/weather")
                .queryParam("q", city)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")
                .build(true)
                .toUri();
        Supplier<ResponseEntity<OpenWeatherResponse>> supplier = () -> restTemplate.getForEntity(uri, OpenWeatherResponse.class);

        Supplier<ResponseEntity<OpenWeatherResponse>> resilientSupplier = CircuitBreaker.decorateSupplier(
                weatherApiCircuitBreaker,
                Retry.decorateSupplier(weatherApiRetry, supplier)
        );

        ResponseEntity<OpenWeatherResponse> resp = resilientSupplier.get();
        OpenWeatherResponse body = resp.getBody();
        if (body == null) {
            return null;
        }

        String name = body.name();
        Double temp = body.main() != null ? body.main().temp() : null;
        String desc = (body.weather() != null && !body.weather().isEmpty())
                ? body.weather().getFirst().description()
                : null;

        WeatherDto dto = new WeatherDto();
        dto.setCity(name);
        dto.setTemperature(temp);
        dto.setDescription(desc);
        return dto;
    }
}
