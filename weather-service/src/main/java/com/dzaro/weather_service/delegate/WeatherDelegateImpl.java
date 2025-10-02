package com.dzaro.weather_service.delegate;

import com.dzaro.weather_service.api.DumpApiDelegate;
import com.dzaro.weather_service.api.HistoryApiDelegate;
import com.dzaro.weather_service.api.WeatherApiDelegate;
import com.dzaro.weather_service.entity.WeatherHistoryEntity;
import com.dzaro.weather_service.model.DumpAcceptedDto;
import com.dzaro.weather_service.model.HistoryEntry;
import com.dzaro.weather_service.model.WeatherDto;
import com.dzaro.weather_service.service.WeatherService;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

@Component
public class WeatherDelegateImpl implements WeatherApiDelegate, HistoryApiDelegate, DumpApiDelegate {

    private final WeatherService service;

    public WeatherDelegateImpl(WeatherService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<WeatherDto> getWeather(String city) {
        WeatherDto dto = service.getWeather(city);
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<List<HistoryEntry>> getHistory(String city, LocalDate dateFrom, LocalDate dateTo) {
        List<WeatherHistoryEntity> history = service.getHistory(city, dateFrom, dateTo);
        return ResponseEntity.ok(toDto(history));
    }

    private List<HistoryEntry> toDto(List<WeatherHistoryEntity> history) {
        System.out.println("Not implemented yet");
        HistoryEntry entry1 = new HistoryEntry();
        entry1.setCity("Paris");
        entry1.setId(1L);
        entry1.setWeatherResponse("{ \"temp\": 20, \"condition\": \"Sunny\" }");
        entry1.setQueryDate(OffsetDateTime.parse("2025-10-01T10:15:30+01:00"));
        List<HistoryEntry> entries = new ArrayList<>();
        entries.add(entry1);
        return entries;
    }

    @Override
    public ResponseEntity<DumpAcceptedDto> requestDataDump() {
        DumpAcceptedDto dto = service.requestDataDump();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(dto);
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }
}
