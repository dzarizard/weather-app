package com.dzaro.file_service.controller;

import com.dzaro.file_service.api.ArchiveApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileController implements ArchiveApi {

    @Override
    public ResponseEntity<Object> archiveCountGet() {
        return ArchiveApi.super.archiveCountGet();
    }
}
