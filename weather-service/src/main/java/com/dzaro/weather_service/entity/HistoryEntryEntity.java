package com.dzaro.weather_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.time.OffsetDateTime;

@Entity
@Table(name = "history_entries")
public class HistoryEntryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private OffsetDateTime queryDate;

    @Column(columnDefinition = "TEXT")
    private String weatherResponseJson;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public OffsetDateTime getQueryDate() {
        return queryDate;
    }

    public void setQueryDate(OffsetDateTime queryDate) {
        this.queryDate = queryDate;
    }

    public String getWeatherResponseJson() {
        return weatherResponseJson;
    }

    public void setWeatherResponseJson(String weatherResponseJson) {
        this.weatherResponseJson = weatherResponseJson;
    }
}

