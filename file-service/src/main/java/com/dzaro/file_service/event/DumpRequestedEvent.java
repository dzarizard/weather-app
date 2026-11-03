package com.dzaro.file_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DumpRequestedEvent {

    private String requestId;
    private String sourceFileName;
    private String payloadJson;
    private OffsetDateTime createdAt;
}