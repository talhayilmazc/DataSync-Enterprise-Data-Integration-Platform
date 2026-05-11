package com.datasync.datasync.service;

import com.datasync.datasync.dto.request.SyncJobRequest;
import com.datasync.datasync.dto.request.SyncJobTriggerRequest;
import com.datasync.datasync.dto.response.MessageResponse;
import com.datasync.datasync.dto.response.SyncJobResponse;
import com.datasync.datasync.dto.response.SyncStatsResponse;

import java.util.List;

public interface SyncJobService {
    SyncJobResponse create(SyncJobRequest request);
    SyncJobResponse getById(Long id);
    SyncJobResponse getByJobId(String jobId);
    List<SyncJobResponse> getAll();
    List<SyncJobResponse> getPendingJobs();
    SyncJobResponse update(Long id, SyncJobRequest request);
    MessageResponse delete(Long id);
    MessageResponse trigger(SyncJobTriggerRequest request);
    MessageResponse cancel(String jobId);
    MessageResponse pause(String jobId);
    MessageResponse resume(String jobId);
    SyncStatsResponse getStats();
}