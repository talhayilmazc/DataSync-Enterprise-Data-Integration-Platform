package com.datasync.datasync.domain.repository;

import com.datasync.datasync.domain.document.SyncEvent;
import com.datasync.datasync.domain.enums.EventType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SyncEventRepository extends MongoRepository<SyncEvent, String> {
    List<SyncEvent> findByJobIdOrderByTimestampDesc(String jobId);
    List<SyncEvent> findByEventType(EventType eventType);
    List<SyncEvent> findByTimestampAfter(LocalDateTime since);
}