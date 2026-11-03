package com.dzaro.weather_service.messaging;

import com.dzaro.weather_service.event.DumpRequestedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DumpEventPublisher {

    private final KafkaTemplate<String, DumpRequestedEvent> kafkaTemplate;

    @Value("${messaging.kafka.topic.dump-requested:weather.dump.requested.v1}")
    private String topic;

    public void publish(DumpRequestedEvent event) {
        kafkaTemplate.send(topic, event.getRequestId(), event);
    }
}