//package com.dzaro.file_service.service;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.nio.file.*;
//import java.time.Instant;
//import java.util.UUID;
//
//@Service
//public class FileStorageService {
//
//    private final Path baseDir;
//
//    public FileStorageService(@Value("${file.base-dir}") String baseDir) throws IOException {
//        this.baseDir = Paths.get(baseDir).toAbsolutePath();
//        Files.createDirectories(this.baseDir);
//    }
//
//    public String saveDump(byte[] jsonBytes) throws IOException {
//        String requestId = UUID.randomUUID().toString();
//        String filename = "dump-" + requestId + "-" + Instant.now().toEpochMilli() + ".json";
//        Path target = baseDir.resolve(filename);
//        Files.write(target, jsonBytes, StandardOpenOption.CREATE_NEW);
//        return requestId;
//    }
//
//    public Path resolveById(String requestId) throws IOException {
//        try (var stream = Files.list(baseDir)) {
//            return stream
//                    .filter(p -> p.getFileName().toString().contains(requestId))
//                    .findFirst()
//                    .orElse(null);
//        }
//    }
//}