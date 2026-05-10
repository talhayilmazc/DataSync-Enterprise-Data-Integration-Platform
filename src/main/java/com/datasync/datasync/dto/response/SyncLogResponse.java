package com.datasync.datasync.dto.response;

import com.datasync.datasync.domain.enums.SyncStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncLogResponse {
    private Long id;
    private String jobId;
    private String jobName;
    private SyncStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Long recordsProcessed;
    private Long recordsFailed;
    private String errorDetails;
    private String triggeredBy;
    private LocalDateTime createdAt;
}