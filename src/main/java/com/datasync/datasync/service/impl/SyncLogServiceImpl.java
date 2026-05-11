package com.datasync.datasync.service.impl;

import com.datasync.datasync.domain.repository.SyncLogRepository;
import com.datasync.datasync.dto.response.SyncLogResponse;
import com.datasync.datasync.exception.ResourceNotFoundException;
import com.datasync.datasync.service.SyncLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SyncLogServiceImpl implements SyncLogService {

    private final SyncLogRepository syncLogRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SyncLogResponse> getByJobId(String jobId) {
        return syncLogRepository.findByJobIdOrderByCreatedAtDesc(jobId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SyncLogResponse> getAll() {
        return syncLogRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SyncLogResponse getById(Long id) {
        return toResponse(syncLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SyncLog", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalRecordsProcessed(String jobId) {
        Long total = syncLogRepository.getTotalRecordsProcessed(jobId);
        return total != null ? total : 0L;
    }

    private SyncLogResponse toResponse(
            com.datasync.datasync.domain.entity.SyncLog log) {
        return SyncLogResponse.builder()
                .id(log.getId())
                .jobId(log.getJobId())
                .jobName(log.getJobName())
                .status(log.getStatus())
                .startedAt(log.getStartedAt())
                .completedAt(log.getCompletedAt())
                .recordsProcessed(log.getRecordsProcessed())
                .recordsFailed(log.getRecordsFailed())
                .errorDetails(log.getErrorDetails())
                .triggeredBy(log.getTriggeredBy())
                .createdAt(log.getCreatedAt())
                .build();
    }
}