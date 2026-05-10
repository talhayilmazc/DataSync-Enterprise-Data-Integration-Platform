package com.datasync.datasync.domain.entity;

import com.datasync.datasync.domain.enums.SyncStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "sync_logs")
public class SyncLog extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String jobId;

    @Column(nullable = false, length = 100)
    private String jobName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SyncStatus status;

    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    @Column(nullable = false)
    @Builder.Default
    private Long recordsProcessed = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long recordsFailed = 0L;

    @Column(columnDefinition = "TEXT")
    private String errorDetails;

    @Column(length = 50)
    private String triggeredBy;
}