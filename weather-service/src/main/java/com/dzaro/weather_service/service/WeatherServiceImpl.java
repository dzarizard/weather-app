package com.dzaro.weather_service.service;

import com.dzaro.weather_service.model.DumpAcceptedDto;
import com.dzaro.weather_service.model.HistoryEntry;
import com.dzaro.weather_service.model.WeatherDto;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class WeatherServiceImpl implements WeatherService {

    @Override
    public WeatherDto getWeather(String city) {
        return null;
    }

    @Override
    public List<HistoryEntry> getHistory(String city, LocalDate from, LocalDate to) {
        return List.of();
    }

    @Override
    public DumpAcceptedDto requestDataDump() {
        return null;
    }
}
