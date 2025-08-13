package com.dzaro.weather_adapter.service;

import com.dzaro.weather_adapter.model.WeatherDto;

public interface AdapterService {
    WeatherDto getWeather(String city);
}
