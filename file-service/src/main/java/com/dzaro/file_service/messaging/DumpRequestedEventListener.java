package com.dzaro.file_service.messaging;

import com.dzaro.file_service.event.DumpRequestedEvent;
import com.dzaro.file_service.service.ArchiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "messaging.kafka.enabled", havingValue = "true")
public class DumpRequestedEventListener {

    private final ArchiveService archiveService;

    @KafkaListener(topics = "${messaging.kafka.topic.dump-requested:weather.dump.requested.v1}")
    public void onEvent(DumpRequestedEvent event) throws Exception {
        archiveService.processDumpEvent(event);
    }
}