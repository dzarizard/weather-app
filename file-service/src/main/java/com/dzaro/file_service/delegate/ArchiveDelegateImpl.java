package com.dzaro.file_service.delegate;

import com.dzaro.file_service.api.ArchiveApiDelegate;
import com.dzaro.file_service.service.ArchiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ArchiveDelegateImpl implements ArchiveApiDelegate {

    private final ArchiveService service;

    @Override
    public ResponseEntity<Object> archiveCountGet() {
        return ResponseEntity.ok(service.getArchiveCount());
    }
}
