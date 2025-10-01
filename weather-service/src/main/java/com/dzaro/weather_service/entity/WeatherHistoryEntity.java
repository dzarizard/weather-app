package com.dzaro.weather_service.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;

@Entity
@Data
@Table(name = "weather_history")
public class WeatherHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String city;
    private OffsetDateTime queryDate;
    @Lob
    @Column(columnDefinition = "CLOB")
    private String weatherResponseJson;

}