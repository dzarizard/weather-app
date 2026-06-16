package com.dzaro.weather_adapter.service;

import java.util.List;

public record OpenWeatherResponse(String name, Main main, List<Weather> weather) {
    public record Main(Double temp) {}
    public record Weather(String description) {}
}