package com.datasync.datasync.domain.entity;

import com.datasync.datasync.domain.enums.SyncStatus;
import com.datasync.datasync.domain.enums.SyncType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "sync_jobs")
public class SyncJob extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String jobId;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", nullable = false)
    private DataSource source;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", nullable = false)
    private DataSource target;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SyncType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SyncStatus status;

    @Column(length = 100)
    private String cronExpression;

    private LocalDateTime lastRunAt;
    private LocalDateTime nextRunAt;

    @Column(nullable = false)
    @Builder.Default
    private Long recordsProcessed = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long recordsFailed = 0L;

    @Column(length = 500)
    private String errorMessage;
}