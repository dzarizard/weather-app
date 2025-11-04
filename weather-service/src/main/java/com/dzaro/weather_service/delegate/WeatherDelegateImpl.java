package com.dzaro.weather_service.delegate;

import com.dzaro.weather_service.api.DumpApiDelegate;
import com.dzaro.weather_service.api.HistoryApiDelegate;
import com.dzaro.weather_service.api.WeatherApiDelegate;
import com.dzaro.weather_service.model.DumpAcceptedDto;
import com.dzaro.weather_service.model.HistoryEntry;
import com.dzaro.weather_service.model.WeatherDto;
import com.dzaro.weather_service.service.WeatherServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class WeatherDelegateImpl implements WeatherApiDelegate, HistoryApiDelegate, DumpApiDelegate {

    private final WeatherServiceImpl service;

    @Override
    public ResponseEntity<WeatherDto> getWeather(String city) {
        WeatherDto dto = service.getWeather(city);
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<List<HistoryEntry>> getHistory(String city, LocalDate dateFrom, LocalDate dateTo) {
        List<HistoryEntry> history = service.getHistory(city, dateFrom, dateTo);
        return ResponseEntity.ok(history);
    }

    @Override
    public ResponseEntity<DumpAcceptedDto> requestDataDump() {
        return ResponseEntity.accepted().body(service.requestDataDump());

    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }
}
