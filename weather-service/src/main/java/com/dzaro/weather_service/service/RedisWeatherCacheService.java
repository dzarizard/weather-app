package com.dzaro.weather_service.service;

import com.dzaro.weather_service.model.WeatherDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisWeatherCacheService {

    private static final Logger log = LoggerFactory.getLogger(RedisWeatherCacheService.class);
    private static final String CACHE_KEY_PREFIX = "weather:latest:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${weather.cache.redis.enabled:true}")
    private boolean redisEnabled;

    @Value("${weather.cache.redis.ttl:10m}")
    private Duration redisTtl;

    public Optional<WeatherDto> get(String normalizedCityKey) {
        if (!redisEnabled) {
            return Optional.empty();
        }

        try {
            String payload = redisTemplate.opsForValue().get(redisKey(normalizedCityKey));
            if (payload == null) {
                return Optional.empty();
            }
            return Optional.of(objectMapper.readValue(payload, WeatherDto.class));
        } catch (Exception ex) {
            log.warn("Could not read weather from Redis for key={}", normalizedCityKey, ex);
            return Optional.empty();
        }
    }

    public void put(String normalizedCityKey, WeatherDto weatherDto) {
        if (!redisEnabled) {
            return;
        }

        try {
            String payload = objectMapper.writeValueAsString(weatherDto);
            redisTemplate.opsForValue().set(redisKey(normalizedCityKey), payload, redisTtl);
        } catch (Exception ex) {
            log.warn("Could not write weather to Redis for key={}", normalizedCityKey, ex);
        }
    }

    private String redisKey(String normalizedCityKey) {
        return CACHE_KEY_PREFIX + normalizedCityKey;
    }
}