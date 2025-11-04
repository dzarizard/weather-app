package com.dzaro.file_service.repository;

import com.dzaro.file_service.entity.WeatherArchiveDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WeatherArchiveRepository extends MongoRepository<WeatherArchiveDocument, String> {

}