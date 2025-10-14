package com.dzaro.file_service.repository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository {
    void saveAllRawJson(List<String> records);
    long count();
}
