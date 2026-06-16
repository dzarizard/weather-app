package com.dzaro.weather_adapter.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.time.Duration;

@Configuration
public class ResilienceConfig {

    @Bean
    CircuitBreaker weatherApiCircuitBreaker(
            @Value("${adapter.resilience.circuit-breaker.failure-rate-threshold:50}") float failureRateThreshold,
            @Value("${adapter.resilience.circuit-breaker.sliding-window-size:4}") int slidingWindowSize,
            @Value("${adapter.resilience.circuit-breaker.minimum-number-of-calls:4}") int minimumNumberOfCalls,
            @Value("${adapter.resilience.circuit-breaker.wait-duration-open-state:30s}") Duration waitDurationInOpenState,
            @Value("${adapter.resilience.circuit-breaker.permitted-calls-in-half-open-state:1}") int permittedCallsInHalfOpenState) {

        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(failureRateThreshold)
                .slidingWindowSize(slidingWindowSize)
                .minimumNumberOfCalls(minimumNumberOfCalls)
                .waitDurationInOpenState(waitDurationInOpenState)
                .permittedNumberOfCallsInHalfOpenState(permittedCallsInHalfOpenState)
                .recordExceptions(ResourceAccessException.class, HttpServerErrorException.class)
                .build();

        return CircuitBreakerRegistry.of(config).circuitBreaker("openWeatherMap");
    }

    @Bean
    Retry weatherApiRetry(
            @Value("${adapter.resilience.retry.max-attempts:3}") int maxAttempts,
            @Value("${adapter.resilience.retry.wait-duration:200ms}") Duration waitDuration) {

        RetryConfig config = RetryConfig.custom()
                .maxAttempts(maxAttempts)
                .waitDuration(waitDuration)
                .retryExceptions(ResourceAccessException.class, HttpServerErrorException.class)
                .failAfterMaxAttempts(true)
                .build();

        return RetryRegistry.of(config).retry("openWeatherMap");
    }
}