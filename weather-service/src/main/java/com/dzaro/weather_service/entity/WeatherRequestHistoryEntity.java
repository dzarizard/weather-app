package com.dzaro.weather_service.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Entity
@Data
@Table(name = "weather_history")
public class WeatherRequestHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "query_date", nullable = false)
    private OffsetDateTime queryDate;

    @Column(name = "weather_response_json", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode weatherResponseJson;

}