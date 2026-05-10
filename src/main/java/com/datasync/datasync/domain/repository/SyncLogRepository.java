package com.datasync.datasync.domain.repository;

import com.datasync.datasync.domain.entity.SyncLog;
import com.datasync.datasync.domain.enums.SyncStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SyncLogRepository extends JpaRepository<SyncLog, Long> {
    List<SyncLog> findByJobIdOrderByCreatedAtDesc(String jobId);
    List<SyncLog> findByStatus(SyncStatus status);

    @Query("SELECT s FROM SyncLog s WHERE s.startedAt >= :since ORDER BY s.startedAt DESC")
    List<SyncLog> findLogsSince(@Param("since") LocalDateTime since);

    @Query("SELECT SUM(s.recordsProcessed) FROM SyncLog s WHERE s.jobId = :jobId")
    Long getTotalRecordsProcessed(@Param("jobId") String jobId);
}