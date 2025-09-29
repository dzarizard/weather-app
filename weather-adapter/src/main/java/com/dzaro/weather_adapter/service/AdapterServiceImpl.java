package com.dzaro.weather_adapter.service;

import com.dzaro.weather_adapter.model.WeatherDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class AdapterServiceImpl implements AdapterService {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiKey;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AdapterServiceImpl() {
        this.restTemplate = null;
        this.baseUrl = null;
        this.apiKey = null;
    }

    public AdapterServiceImpl(RestTemplate restTemplate, String baseUrl, String apiKey) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    @Override
    public WeatherDto getWeather(String city) {
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City cannot be null or empty");
        }

        if (restTemplate == null || baseUrl == null || baseUrl.isBlank()) {
            return new WeatherDto()
                    .city(city)
                    .temperature(20.0)
                    .description("Stubbed");
        }

        try {
            String url = baseUrl + "/data/2.5/weather?q=" + URLEncoder.encode(city, StandardCharsets.UTF_8) + "&appid=" + apiKey;
            ResponseEntity<String> resp = restTemplate.getForEntity(url, String.class);
            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                throw new HttpClientErrorException(resp.getStatusCode());
            }
            JsonNode json = objectMapper.readTree(resp.getBody());
            String name = json.path("name").asText(city);
            double temp = json.path("main").path("temp").asDouble();
            String desc = json.path("weather").isArray() && json.path("weather").size() > 0
                    ? json.path("weather").get(0).path("description").asText()
                    : null;
            return new WeatherDto()
                    .city(name)
                    .temperature(temp)
                    .description(desc);
        } catch (HttpClientErrorException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
