package com.datasync.datasync.kafka.producer;

import com.datasync.datasync.domain.document.SyncEvent;
import com.datasync.datasync.domain.enums.EventType;
import com.datasync.datasync.domain.enums.SyncStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class SyncEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public static final String SYNC_EVENTS_TOPIC = "sync-events";
    public static final String SYNC_COMPLETED_TOPIC = "sync-completed";
    public static final String SYNC_FAILED_TOPIC = "sync-failed";

    public void publishSyncStarted(String jobId, String jobName, String triggeredBy) {
        SyncEvent event = SyncEvent.builder()
                .jobId(jobId)
                .jobName(jobName)
                .eventType(EventType.SYNC_STARTED)
                .status(SyncStatus.RUNNING)
                .timestamp(LocalDateTime.now())
                .triggeredBy(triggeredBy)
                .build();
        publish(SYNC_EVENTS_TOPIC, jobId, event);
        log.info("Sync started event published for job: {}", jobId);
    }

    public void publishSyncCompleted(String jobId, String jobName,
                                      Long recordsProcessed, Long recordsFailed) {
        SyncEvent event = SyncEvent.builder()
                .jobId(jobId)
                .jobName(jobName)
                .eventType(EventType.SYNC_COMPLETED)
                .status(SyncStatus.COMPLETED)
                .timestamp(LocalDateTime.now())
                .recordsProcessed(recordsProcessed)
                .recordsFailed(recordsFailed)
                .build();
        publish(SYNC_COMPLETED_TOPIC, jobId, event);
        log.info("Sync completed event published for job: {}", jobId);
    }

    public void publishSyncFailed(String jobId, String jobName, String errorMessage) {
        SyncEvent event = SyncEvent.builder()
                .jobId(jobId)
                .jobName(jobName)
                .eventType(EventType.SYNC_FAILED)
                .status(SyncStatus.FAILED)
                .timestamp(LocalDateTime.now())
                .errorMessage(errorMessage)
                .build();
        publish(SYNC_FAILED_TOPIC, jobId, event);
        log.error("Sync failed event published for job: {}", jobId);
    }

    private void publish(String topic, String key, SyncEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, key, payload);
        } catch (Exception e) {
            log.error("Failed to publish event for job: {}", key, e);
        }
    }
}