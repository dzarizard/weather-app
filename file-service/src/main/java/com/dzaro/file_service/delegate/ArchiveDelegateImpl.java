package com.dzaro.file_service.delegate;

import com.dzaro.file_service.api.ArchiveApiDelegate;
import com.dzaro.file_service.repository.FileRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ArchiveDelegateImpl implements ArchiveApiDelegate {

    private final FileRepository repo;

    public ArchiveDelegateImpl(FileRepository repo) {
        this.repo = repo;
    }

    @Override
    public ResponseEntity<Object> archiveCountGet() {
        return ResponseEntity.ok(repo.count());
    }
}
