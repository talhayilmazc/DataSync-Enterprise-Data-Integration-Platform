package com.datasync.datasync.domain.document;

import com.datasync.datasync.domain.enums.EventType;
import com.datasync.datasync.domain.enums.SyncStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "sync_events")
public class SyncEvent {

    @Id
    private String id;

    private String jobId;
    private String jobName;

    private EventType eventType;
    private SyncStatus status;

    private LocalDateTime timestamp;

    private Long recordsProcessed;
    private Long recordsFailed;

    private String errorMessage;
    private String triggeredBy;

    private Map<String, Object> metadata;
}