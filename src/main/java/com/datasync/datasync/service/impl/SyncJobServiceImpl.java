package com.datasync.datasync.service.impl;

import com.datasync.datasync.domain.entity.DataSource;
import com.datasync.datasync.domain.entity.SyncJob;
import com.datasync.datasync.domain.entity.SyncLog;
import com.datasync.datasync.domain.enums.SyncStatus;
import com.datasync.datasync.domain.repository.DataSourceRepository;
import com.datasync.datasync.domain.repository.SyncJobRepository;
import com.datasync.datasync.domain.repository.SyncLogRepository;
import com.datasync.datasync.dto.request.SyncJobRequest;
import com.datasync.datasync.dto.request.SyncJobTriggerRequest;
import com.datasync.datasync.dto.response.MessageResponse;
import com.datasync.datasync.dto.response.SyncJobResponse;
import com.datasync.datasync.dto.response.SyncStatsResponse;
import com.datasync.datasync.exception.BusinessException;
import com.datasync.datasync.exception.ResourceNotFoundException;
import com.datasync.datasync.exception.SyncException;
import com.datasync.datasync.kafka.producer.SyncEventProducer;
import com.datasync.datasync.service.SyncJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyncJobServiceImpl implements SyncJobService {

    private final SyncJobRepository syncJobRepository;
    private final SyncLogRepository syncLogRepository;
    private final DataSourceRepository dataSourceRepository;
    private final SyncEventProducer syncEventProducer;

    @Override
    @Transactional
    public SyncJobResponse create(SyncJobRequest request) {
        DataSource source = dataSourceRepository.findById(request.getSourceId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Kaynak veri kaynağı", request.getSourceId()));
        DataSource target = dataSourceRepository.findById(request.getTargetId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Hedef veri kaynağı", request.getTargetId()));

        SyncJob job = SyncJob.builder()
                .jobId(UUID.randomUUID().toString())
                .name(request.getName())
                .source(source)
                .target(target)
                .type(request.getType())
                .status(SyncStatus.PENDING)
                .cronExpression(request.getCronExpression())
                .recordsProcessed(0L)
                .recordsFailed(0L)
                .build();

        SyncJob saved = syncJobRepository.save(job);
        log.info("SyncJob created: {}", saved.getJobId());
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public SyncJobResponse getById(Long id) {
        return toResponse(syncJobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SyncJob", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public SyncJobResponse getByJobId(String jobId) {
        return toResponse(syncJobRepository.findByJobId(jobId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "SyncJob bulunamadı: " + jobId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SyncJobResponse> getAll() {
        return syncJobRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SyncJobResponse> getPendingJobs() {
        return syncJobRepository.findPendingJobs()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SyncJobResponse update(Long id, SyncJobRequest request) {
        SyncJob job = syncJobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SyncJob", id));
        job.setName(request.getName());
        job.setType(request.getType());
        job.setCronExpression(request.getCronExpression());
        return toResponse(syncJobRepository.save(job));
    }

    @Override
    @Transactional
    public MessageResponse delete(Long id) {
        SyncJob job = syncJobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SyncJob", id));
        if (job.getStatus() == SyncStatus.RUNNING) {
            throw new BusinessException(
                    "Çalışan bir job silinemez", "JOB_RUNNING");
        }
        job.setDeleted(true);
        syncJobRepository.save(job);
        return MessageResponse.success("SyncJob başarıyla silindi");
    }

    @Override
    @Transactional
    public MessageResponse trigger(SyncJobTriggerRequest request) {
        SyncJob job = syncJobRepository.findByJobId(request.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "SyncJob bulunamadı: " + request.getJobId()));

        if (job.getStatus() == SyncStatus.RUNNING) {
            throw new SyncException("Job zaten çalışıyor", "JOB_ALREADY_RUNNING");
        }

        job.setStatus(SyncStatus.RUNNING);
        job.setLastRunAt(LocalDateTime.now());
        syncJobRepository.save(job);

        SyncLog syncLog = SyncLog.builder()
                .jobId(job.getJobId())
                .jobName(job.getName())
                .status(SyncStatus.RUNNING)
                .startedAt(LocalDateTime.now())
                .recordsProcessed(0L)
                .recordsFailed(0L)
                .triggeredBy(request.getTriggeredBy())
                .build();
        syncLogRepository.save(syncLog);

        syncEventProducer.publishSyncStarted(
                job.getJobId(), job.getName(), request.getTriggeredBy());

        log.info("SyncJob triggered: {}", job.getJobId());
        return MessageResponse.success("SyncJob başlatıldı: " + job.getJobId());
    }

    @Override
    @Transactional
    public MessageResponse cancel(String jobId) {
        SyncJob job = syncJobRepository.findByJobId(jobId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "SyncJob bulunamadı: " + jobId));
        job.setStatus(SyncStatus.CANCELLED);
        syncJobRepository.save(job);
        return MessageResponse.success("SyncJob iptal edildi");
    }

    @Override
    @Transactional
    public MessageResponse pause(String jobId) {
        SyncJob job = syncJobRepository.findByJobId(jobId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "SyncJob bulunamadı: " + jobId));
        job.setStatus(SyncStatus.PAUSED);
        syncJobRepository.save(job);
        return MessageResponse.success("SyncJob duraklatıldı");
    }

    @Override
    @Transactional
    public MessageResponse resume(String jobId) {
        SyncJob job = syncJobRepository.findByJobId(jobId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "SyncJob bulunamadı: " + jobId));
        job.setStatus(SyncStatus.PENDING);
        syncJobRepository.save(job);
        return MessageResponse.success("SyncJob devam ettiriliyor");
    }

    @Override
    @Transactional(readOnly = true)
    public SyncStatsResponse getStats() {
        List<SyncJob> allJobs = syncJobRepository.findAll();
        return SyncStatsResponse.builder()
                .totalJobs((long) allJobs.size())
                .activeJobs(allJobs.stream()
                        .filter(j -> j.getStatus() == SyncStatus.RUNNING).count())
                .completedJobs(allJobs.stream()
                        .filter(j -> j.getStatus() == SyncStatus.COMPLETED).count())
                .failedJobs(allJobs.stream()
                        .filter(j -> j.getStatus() == SyncStatus.FAILED).count())
                .totalRecordsProcessed(allJobs.stream()
                        .mapToLong(SyncJob::getRecordsProcessed).sum())
                .totalDataSources(dataSourceRepository.count())
                .activeDataSources(dataSourceRepository.findByActiveTrue().size())
                .build();
    }

    private SyncJobResponse toResponse(SyncJob job) {
        return SyncJobResponse.builder()
                .id(job.getId())
                .jobId(job.getJobId())
                .name(job.getName())
                .sourceName(job.getSource().getName())
                .targetName(job.getTarget().getName())
                .type(job.getType())
                .status(job.getStatus())
                .cronExpression(job.getCronExpression())
                .lastRunAt(job.getLastRunAt())
                .nextRunAt(job.getNextRunAt())
                .recordsProcessed(job.getRecordsProcessed())
                .recordsFailed(job.getRecordsFailed())
                .errorMessage(job.getErrorMessage())
                .createdAt(job.getCreatedAt())
                .build();
    }
}