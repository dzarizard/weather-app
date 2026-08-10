package com.dzaro.weather_service.config;

import com.dzaro.weather_service.model.WeatherDto;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class WeatherServiceConfig {

    @Bean
    Cache<String, WeatherDto> latestWeatherCache(
            @Value("${weather.cache.maximum-size:1000}") long maximumSize,
            @Value("${weather.cache.expire-after-write:10m}") Duration expireAfterWrite) {
        return Caffeine.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterWrite(expireAfterWrite)
                .build();
    }

    @Bean
    RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(2));
        factory.setReadTimeout(Duration.ofSeconds(3));

        return new RestTemplate(factory);
    }
}
