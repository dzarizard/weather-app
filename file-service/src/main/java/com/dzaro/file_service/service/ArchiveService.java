package com.dzaro.file_service.service;

import com.dzaro.file_service.entity.WeatherArchiveDocument;
import com.dzaro.file_service.repository.WeatherArchiveRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ArchiveService {

    private final WeatherArchiveRepository repository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void processJson(String fileName, String jsonContent) throws Exception {
        JsonNode payload = objectMapper.readValue(jsonContent, new TypeReference<>() {});
        WeatherArchiveDocument doc = WeatherArchiveDocument.builder()
                .payload(payload)
                .sourceFileName(fileName)
                .processedAt(Instant.now())
                .build();
        repository.save(doc);
    }

    public long getArchiveCount() {
        return repository.count();
    }
}
