package com.dzaro.file_service.controller;

import com.dzaro.file_service.api.ArchiveApiDelegate;
import com.dzaro.file_service.service.ArchiveService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ArchiveDelegateImpl implements ArchiveApiDelegate {

    private final ArchiveService archiveService;

    public ArchiveDelegateImpl(ArchiveService archiveService) {
        this.archiveService = archiveService;
    }

    @Override
    public ResponseEntity<Object> archiveCountGet() {
        long count = archiveService.getArchiveCount();
        return ResponseEntity.ok(Map.of("count", count));
    }
}
