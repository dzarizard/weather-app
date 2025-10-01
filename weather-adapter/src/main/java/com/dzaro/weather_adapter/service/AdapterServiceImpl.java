package com.dzaro.weather_adapter.service;

import com.dzaro.weather_adapter.model.WeatherDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Service
public class AdapterServiceImpl implements AdapterService {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiKey;

    public AdapterServiceImpl(RestTemplate restTemplate,
                              @Value("${adapter.external.base-url}") String baseUrl,
                              @Value("${adapter.external.api-key}") String apiKey) {
        this.restTemplate = restTemplate;
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

        ResponseEntity<Map> resp = restTemplate.getForEntity(uri, Map.class);

        Map body = resp.getBody();
        if (body == null) {
            return null;
        }

        String name = (String) body.get("name");
        Double temp = null;
        Object main = body.get("main");
        if (main instanceof Map<?, ?> m) {
            Object temperature = m.get("temp");
            if (temperature instanceof Number n) {
                temp = n.doubleValue();
            }
        }

        String desc = null;
        Object weather = body.get("weather");
        if (weather instanceof List<?> list && !list.isEmpty() && list.getFirst() instanceof Map<?, ?> weatherMap) {
            Object description = weatherMap.get("description");
            if (description != null) {
                desc = description.toString();
            }
        }

        WeatherDto dto = new WeatherDto();
        dto.setCity(name);
        dto.setTemperature(temp);
        dto.setDescription(desc);
        return dto;
    }

}
