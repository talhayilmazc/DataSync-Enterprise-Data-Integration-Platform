package com.datasync.datasync.domain.repository;

import com.datasync.datasync.domain.entity.SyncJob;
import com.datasync.datasync.domain.enums.SyncStatus;
import com.datasync.datasync.domain.enums.SyncType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SyncJobRepository extends JpaRepository<SyncJob, Long> {
    Optional<SyncJob> findByJobId(String jobId);
    List<SyncJob> findByStatus(SyncStatus status);
    List<SyncJob> findByType(SyncType type);
    List<SyncJob> findBySourceId(Long sourceId);
    List<SyncJob> findByTargetId(Long targetId);
    boolean existsByJobId(String jobId);

    @Query("SELECT s FROM SyncJob s WHERE s.status = 'PENDING' ORDER BY s.createdAt ASC")
    List<SyncJob> findPendingJobs();
}