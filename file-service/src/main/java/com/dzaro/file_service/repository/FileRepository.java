package com.dzaro.file_service.repository;

import org.springframework.stereotype.Repository;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class FileRepository {
    
    private final AtomicLong archiveCount = new AtomicLong(0);
    
    public long getArchiveCount() {
        return archiveCount.get();
    }
    
    public void incrementArchiveCount() {
        archiveCount.incrementAndGet();
    }
    
    public void resetArchiveCount() {
        archiveCount.set(0);
    }
}
