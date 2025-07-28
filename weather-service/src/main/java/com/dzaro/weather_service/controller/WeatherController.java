package com.dzaro.weather_service.controller;

import com.dzaro.weather_service.api.WeatherApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController implements WeatherApi {

    @Override
    public ResponseEntity<Object> getWeather(String city) {
        return WeatherApi.super.getWeather(city);
    }
}
