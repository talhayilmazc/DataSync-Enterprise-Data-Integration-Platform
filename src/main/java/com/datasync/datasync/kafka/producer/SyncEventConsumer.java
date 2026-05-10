package com.datasync.datasync.kafka.consumer;

import com.datasync.datasync.domain.document.SyncEvent;
import com.datasync.datasync.domain.repository.SyncEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SyncEventConsumer {

    private final SyncEventRepository syncEventRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "sync-events",
            groupId = "datasync-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeSyncEvent(String message) {
        try {
            SyncEvent event = objectMapper.readValue(message, SyncEvent.class);
            syncEventRepository.save(event);
            log.info("Sync event consumed and saved: {} - {}",
                    event.getJobId(), event.getEventType());
        } catch (Exception e) {
            log.error("Failed to consume sync event: {}", message, e);
        }
    }

    @KafkaListener(
            topics = "sync-completed",
            groupId = "datasync-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeSyncCompleted(String message) {
        try {
            SyncEvent event = objectMapper.readValue(message, SyncEvent.class);
            syncEventRepository.save(event);
            log.info("Sync completed event consumed: {} — processed: {}, failed: {}",
                    event.getJobId(),
                    event.getRecordsProcessed(),
                    event.getRecordsFailed());
        } catch (Exception e) {
            log.error("Failed to consume sync completed event", e);
        }
    }

    @KafkaListener(
            topics = "sync-failed",
            groupId = "datasync-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeSyncFailed(String message) {
        try {
            SyncEvent event = objectMapper.readValue(message, SyncEvent.class);
            syncEventRepository.save(event);
            log.error("Sync failed event consumed: {} — error: {}",
                    event.getJobId(), event.getErrorMessage());
        } catch (Exception e) {
            log.error("Failed to consume sync failed event", e);
        }
    }
}