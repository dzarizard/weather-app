package com.dzaro.weather_adapter.delegate;


import com.dzaro.weather_adapter.api.AdapterApiDelegate;
import com.dzaro.weather_adapter.model.WeatherDto;
import com.dzaro.weather_adapter.service.AdapterService;
import java.math.BigDecimal;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class AdapterDelegateImpl implements AdapterApiDelegate {

    private final AdapterService adapterService;

    public AdapterDelegateImpl(AdapterService adapterService) {
        this.adapterService = adapterService;
    }

    @Override
    public ResponseEntity<WeatherDto> adapterWeatherGet(String city) {
        WeatherDto dto = adapterService.getWeather(city);
        return ResponseEntity.ok(dto);
    }
}
