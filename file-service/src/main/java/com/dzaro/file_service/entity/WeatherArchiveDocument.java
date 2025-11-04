package com.dzaro.file_service.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Document("weather_archive")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherArchiveDocument {

    @Id
    private String id;
    private Map<String, Object> payload;
    private String sourceFileName;
    private Instant processedAt;
}