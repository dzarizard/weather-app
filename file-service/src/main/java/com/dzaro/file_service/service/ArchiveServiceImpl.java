package com.dzaro.file_service.service;

import com.dzaro.file_service.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArchiveServiceImpl implements ArchiveService {

    private final FileRepository fileRepository;

    @Autowired
    public ArchiveServiceImpl(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public long getArchiveCount() {
        return fileRepository.getArchiveCount();
    }
}
