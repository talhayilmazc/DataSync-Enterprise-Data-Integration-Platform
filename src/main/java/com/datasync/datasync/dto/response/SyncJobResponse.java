package com.datasync.datasync.dto.response;

import com.datasync.datasync.domain.enums.SyncStatus;
import com.datasync.datasync.domain.enums.SyncType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncJobResponse {
    private Long id;
    private String jobId;
    private String name;
    private String sourceName;
    private String targetName;
    private SyncType type;
    private SyncStatus status;
    private String cronExpression;
    private LocalDateTime lastRunAt;
    private LocalDateTime nextRunAt;
    private Long recordsProcessed;
    private Long recordsFailed;
    private String errorMessage;
    private LocalDateTime createdAt;
}