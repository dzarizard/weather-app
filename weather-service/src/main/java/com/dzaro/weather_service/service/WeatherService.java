package com.dzaro.weather_service.service;

import com.dzaro.weather_service.entity.WeatherHistoryEntity;
import com.dzaro.weather_service.model.DumpAcceptedDto;
import com.dzaro.weather_service.model.WeatherDto;
import java.time.LocalDate;
import java.util.List;

public interface WeatherService {

    WeatherDto getWeather(String city);
    List<WeatherHistoryEntity> getHistory(String city, LocalDate from, LocalDate to);
    DumpAcceptedDto requestDataDump();

}
