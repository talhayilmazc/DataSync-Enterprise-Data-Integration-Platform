package com.datasync.datasync.service;

import com.datasync.datasync.domain.entity.DataSource;
import com.datasync.datasync.domain.entity.SyncJob;
import com.datasync.datasync.domain.enums.DataSourceType;
import com.datasync.datasync.domain.enums.SyncStatus;
import com.datasync.datasync.domain.enums.SyncType;
import com.datasync.datasync.domain.repository.DataSourceRepository;
import com.datasync.datasync.domain.repository.SyncJobRepository;
import com.datasync.datasync.domain.repository.SyncLogRepository;
import com.datasync.datasync.dto.request.SyncJobRequest;
import com.datasync.datasync.dto.request.SyncJobTriggerRequest;
import com.datasync.datasync.dto.response.MessageResponse;
import com.datasync.datasync.dto.response.SyncJobResponse;
import com.datasync.datasync.exception.BusinessException;
import com.datasync.datasync.exception.ResourceNotFoundException;
import com.datasync.datasync.kafka.producer.SyncEventProducer;
import com.datasync.datasync.service.impl.SyncJobServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
@DisplayName("SyncJob Service Tests")
class SyncJobServiceTest {

    @Mock private SyncJobRepository syncJobRepository;
    @Mock private SyncLogRepository syncLogRepository;
    @Mock private DataSourceRepository dataSourceRepository;
    @Mock private SyncEventProducer syncEventProducer;
    @InjectMocks private SyncJobServiceImpl syncJobService;

    private DataSource sourceDs;
    private DataSource targetDs;
    private SyncJob testJob;
    private SyncJobRequest testRequest;

    @BeforeEach
    void setUp() {
        sourceDs = DataSource.builder()
                .name("Source DB")
                .type(DataSourceType.POSTGRESQL)
                .connectionUrl("jdbc:postgresql://localhost:5432/source")
                .status(SyncStatus.COMPLETED)
                .active(true)
                .retryCount(0)
                .build();

        targetDs = DataSource.builder()
                .name("Target DB")
                .type(DataSourceType.MONGODB)
                .connectionUrl("mongodb://localhost:27017/target")
                .status(SyncStatus.COMPLETED)
                .active(true)
                .retryCount(0)
                .build();

        testJob = SyncJob.builder()
                .jobId("test-job-id")
                .name("Test Sync Job")
                .source(sourceDs)
                .target(targetDs)
                .type(SyncType.FULL)
                .status(SyncStatus.PENDING)
                .recordsProcessed(0L)
                .recordsFailed(0L)
                .build();

        testRequest = SyncJobRequest.builder()
                .name("Test Sync Job")
                .sourceId(1L)
                .targetId(2L)
                .type(SyncType.FULL)
                .build();
    }

    @Test
    @DisplayName("Sync job başarıyla oluşturulmalı")
    void create_ShouldCreateSyncJob_WhenValidRequest() {
        when(dataSourceRepository.findById(1L)).thenReturn(Optional.of(sourceDs));
        when(dataSourceRepository.findById(2L)).thenReturn(Optional.of(targetDs));
        when(syncJobRepository.save(any(SyncJob.class))).thenReturn(testJob);

        SyncJobResponse response = syncJobService.create(testRequest);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Test Sync Job");
        verify(syncJobRepository, times(1)).save(any(SyncJob.class));
    }

    @Test
    @DisplayName("Olmayan kaynak ID ile job oluşturulamamalı")
    void create_ShouldThrowException_WhenSourceNotFound() {
        when(dataSourceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> syncJobService.create(testRequest))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(syncJobRepository, never()).save(any());
    }

    @Test
    @DisplayName("Job ID ile sync job getirilmeli")
    void getByJobId_ShouldReturnJob_WhenExists() {
        when(syncJobRepository.findByJobId("test-job-id"))
                .thenReturn(Optional.of(testJob));

        SyncJobResponse response = syncJobService.getByJobId("test-job-id");

        assertThat(response).isNotNull();
        assertThat(response.getJobId()).isEqualTo("test-job-id");
    }

    @Test
    @DisplayName("Sync job başarıyla tetiklenmeli")
    void trigger_ShouldTriggerJob_WhenPending() {
        SyncJobTriggerRequest triggerRequest = SyncJobTriggerRequest.builder()
                .jobId("test-job-id")
                .triggeredBy("admin")
                .build();

        when(syncJobRepository.findByJobId("test-job-id"))
                .thenReturn(Optional.of(testJob));
        when(syncJobRepository.save(any(SyncJob.class))).thenReturn(testJob);
        when(syncLogRepository.save(any())).thenReturn(null);
        doNothing().when(syncEventProducer)
                .publishSyncStarted(any(), any(), any());

        MessageResponse response = syncJobService.trigger(triggerRequest);

        assertThat(response.isSuccess()).isTrue();
        verify(syncEventProducer, times(1))
                .publishSyncStarted(any(), any(), any());
    }

    @Test
    @DisplayName("Çalışan job tekrar tetiklenememeli")
    void trigger_ShouldThrowException_WhenAlreadyRunning() {
        testJob.setStatus(SyncStatus.RUNNING);
        SyncJobTriggerRequest triggerRequest = SyncJobTriggerRequest.builder()
                .jobId("test-job-id")
                .triggeredBy("admin")
                .build();

        when(syncJobRepository.findByJobId("test-job-id"))
                .thenReturn(Optional.of(testJob));

        assertThatThrownBy(() -> syncJobService.trigger(triggerRequest))
                .isInstanceOf(com.datasync.datasync.exception.SyncException.class);
    }

    @Test
    @DisplayName("Çalışan job silinemez")
    void delete_ShouldThrowException_WhenRunning() {
        testJob.setStatus(SyncStatus.RUNNING);
        when(syncJobRepository.findById(1L)).thenReturn(Optional.of(testJob));

        assertThatThrownBy(() -> syncJobService.delete(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("silinemez");
    }
}