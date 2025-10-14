//package com.dzaro.file_service.controller;
//
//import com.dzaro.file_service.dto.DumpAcceptedDto;
//import com.dzaro.file_service.service.FileStorageService;
//import org.springframework.http.*;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//import java.nio.file.*;
//
//@RestController
//@RequestMapping("/dump")
//public class DumpController {
//
//    private final FileStorageService storage;
//
//    public DumpController(FileStorageService storage) {
//        this.storage = storage;
//    }
//
//    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<DumpAcceptedDto> createDump(@RequestBody byte[] body) {
//        if (body == null || body.length == 0) {
//            return ResponseEntity.badRequest().build();
//        }
//        try {
//            String id = storage.saveDump(body);
//            return ResponseEntity.accepted().body(new DumpAcceptedDto(id));
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//    @GetMapping(value = "/{requestId}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<byte[]> getDump(@PathVariable String requestId) throws IOException {
//        Path p = storage.resolveById(requestId);
//        if (p == null || !Files.exists(p)) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//        byte[] bytes = Files.readAllBytes(p);
//        return ResponseEntity.ok()
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(bytes);
//    }
//}