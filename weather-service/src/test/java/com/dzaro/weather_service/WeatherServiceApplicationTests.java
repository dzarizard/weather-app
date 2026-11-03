package com.dzaro.weather_service;

import com.dzaro.weather_service.repository.IdempotencyRecordRepository;
import com.dzaro.weather_service.repository.WeatherHistoryRequestRepository;
import com.dzaro.weather_service.service.RedisWeatherCacheService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = {
		"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration",
		"spring.sql.init.mode=never"
})
class WeatherServiceApplicationTests {

	@MockitoBean
	WeatherHistoryRequestRepository weatherHistoryRequestRepository;

	@MockitoBean
	IdempotencyRecordRepository idempotencyRecordRepository;

	@MockitoBean
	RedisWeatherCacheService redisWeatherCacheService;

	@Test
	void contextLoads() {
	}

}
