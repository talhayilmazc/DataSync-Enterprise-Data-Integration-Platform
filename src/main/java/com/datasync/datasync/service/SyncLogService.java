package com.datasync.datasync.service;

import com.datasync.datasync.dto.response.SyncLogResponse;

import java.util.List;

public interface SyncLogService {
    List<SyncLogResponse> getByJobId(String jobId);
    List<SyncLogResponse> getAll();
    SyncLogResponse getById(Long id);
    Long getTotalRecordsProcessed(String jobId);
}