package com.dzaro.weather_adapter.controller;

import com.dzaro.weather_adapter.api.AdapterApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherAdapterController implements AdapterApi {

    @Override
    public ResponseEntity<Object> adapterWeatherGet(String city) {
        return AdapterApi.super.adapterWeatherGet(city);
    }
}
