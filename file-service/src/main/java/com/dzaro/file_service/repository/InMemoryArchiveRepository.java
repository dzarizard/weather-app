package com.dzaro.file_service.repository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryArchiveRepository implements FileRepository {

    private final AtomicLong counter = new AtomicLong(0);

    @Override
    public void saveAllRawJson(List<String> records) {
        if (records != null) {
          counter.addAndGet(records.size());
        }
    }

    @Override
    public long count() {
        return counter.get();
    }
}