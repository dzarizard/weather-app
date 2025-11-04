package com.dzaro.file_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileSchedulerService {

    private final Path inboxDir;
    private final ArchiveService archiveService;

    @Scheduled(cron = "*/10 * * * * *")
    public void scanForFiles() {
        try {
            System.out.println("checking for directory");
            if (Files.notExists(inboxDir)) {
                System.out.println("directory not exists, creating new one");
                Files.createDirectories(inboxDir);
            }
            System.out.println("Found directory: " + inboxDir.toAbsolutePath());
            List<Path> files = listMatchingFiles(inboxDir);
            if (files.isEmpty()) {
                return;
            }

            for (Path file : files) {
                try {
                    System.out.println("saving json");
                    archiveService.processJson(String.valueOf(file.getFileName()), Files.readString(file, StandardCharsets.UTF_8).trim());
                    System.out.println("removing file");
                    Files.deleteIfExists(file);
                } catch (Exception ex) {
                    System.out.println("found corrupted files");
                }
            }
        } catch (IOException e) {
            System.err.println("FileService scan error: " + e.getMessage());
        }
    }

    private static List<Path> listMatchingFiles(Path dir) throws IOException {
        try (var s = Files.list(dir)) {
            return s.filter(Files::isRegularFile)
                    .filter(p -> {
                        String name = p.getFileName().toString().toLowerCase(Locale.ROOT);
                        return name.endsWith(".json");
                    })
                    .collect(Collectors.toList());
        }
    }
}