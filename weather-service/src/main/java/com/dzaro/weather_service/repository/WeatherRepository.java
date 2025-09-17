package com.dzaro.weather_service.repository;

import com.dzaro.weather_service.model.HistoryEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Repository
public class WeatherRepository {

    private final WeatherHistoryJpaRepository jpaRepository;

    @Autowired
    public WeatherRepository(WeatherHistoryJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    public void save(HistoryEntry entity) {
        jpaRepository.save(entity);
    }
    
    public List<HistoryEntry> findByCityAndDateRange(String city, LocalDate from, LocalDate to) {
        OffsetDateTime fromDateTime = from != null ? from.atStartOfDay().atOffset(ZoneOffset.UTC) : null;
        OffsetDateTime toDateTime = to != null ? to.atTime(23, 59, 59).atOffset(ZoneOffset.UTC) : null;
        
        return jpaRepository.findByCityAndDateRange(city, fromDateTime, toDateTime);
    }
    
    public List<HistoryEntry> findAll() {
        return jpaRepository.findAllByOrderByQueryDateDesc();
    }
}
