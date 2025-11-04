package com.dzaro.file_service.entity;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("weather_archive")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherArchiveDocument {

    @Id
    private String id;
    private JsonNode payload;
    private String sourceFileName;
    private Instant processedAt;
}