package com.dzaro.weather_adapter.delegate;


import com.dzaro.weather_adapter.api.AdapterApiDelegate;
import com.dzaro.weather_adapter.model.WeatherDto;
import com.dzaro.weather_adapter.service.AdapterService;
import java.math.BigDecimal;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class AdapterDelegateImpl implements AdapterApiDelegate {

    @Override
    public ResponseEntity<WeatherDto> adapterWeatherGet(String city) {
        WeatherDto dto = new WeatherDto()
                .city(city)
                .temperature(25.5)
                .description("Sunny");
        return ResponseEntity.ok(dto);
    }
}
