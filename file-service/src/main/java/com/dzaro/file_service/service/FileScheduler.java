package com.dzaro.file_service.service;

import com.dzaro.file_service.repository.FileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FileScheduler {

    private final Path inboxDir;
    private final FileRepository repository;
    private final ObjectMapper objectMapper;

    public FileScheduler(Path inboxDir, FileRepository repository, ObjectMapper objectMapper) {
        this.inboxDir = inboxDir;
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

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
                    List<String> records = readAsRecords(file);
                    if (!records.isEmpty()) {
                        System.out.println("saving json and incrementing counter");
                        repository.saveAllRawJson(records);
                    }
                    System.out.println("removing file");
                    Files.deleteIfExists(file);
                } catch (Exception ex) {
                    System.out.println("file needs to be moved as its not correct");
//                    moveToFailed(file, ex);
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

    private List<String> readAsRecords(Path file) throws IOException {
        String content = Files.readString(file, StandardCharsets.UTF_8).trim();
        if (content.isEmpty()) {
            return List.of();
        }
        parseJson(content);
        return List.of(content);
    }

    private void parseJson(String json) throws IOException {
        objectMapper.readTree(json);
    }

//    private void moveToFailed(Path file, Exception ex) {
//        try {
//            Path failedDir = inboxDir.resolve("failed");
//            Files.createDirectories(failedDir);
//            Path target = failedDir.resolve(file.getFileName().toString() + ".err");
//            Files.move(file, target, StandardCopyOption.REPLACE_EXISTING);
//            Files.writeString(
//                    failedDir.resolve(file.getFileName() + ".log"),
//                    "File moving error: " + ex.getMessage(),
//                    StandardCharsets.UTF_8,
//                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
//            );
//        } catch (IOException e) {
//            System.err.println("FileService failed to move file: " + e.getMessage());
//        }
//    }
}