package com.datasync.datasync.service;

import com.datasync.datasync.domain.entity.DataSource;
import com.datasync.datasync.domain.enums.DataSourceType;
import com.datasync.datasync.domain.enums.SyncStatus;
import com.datasync.datasync.domain.repository.DataSourceRepository;
import com.datasync.datasync.dto.request.DataSourceRequest;
import com.datasync.datasync.dto.response.DataSourceResponse;
import com.datasync.datasync.exception.BusinessException;
import com.datasync.datasync.exception.ResourceNotFoundException;
import com.datasync.datasync.service.impl.DataSourceServiceImpl;
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
@DisplayName("DataSource Service Tests")
class DataSourceServiceTest {

    @Mock private DataSourceRepository dataSourceRepository;
    @InjectMocks private DataSourceServiceImpl dataSourceService;

    private DataSource testDataSource;
    private DataSourceRequest testRequest;

    @BeforeEach
    void setUp() {
        testDataSource = DataSource.builder()
                .name("Test PostgreSQL")
                .description("Test veritabanı")
                .type(DataSourceType.POSTGRESQL)
                .connectionUrl("jdbc:postgresql://localhost:5432/test")
                .status(SyncStatus.PENDING)
                .active(true)
                .retryCount(0)
                .build();

        testRequest = DataSourceRequest.builder()
                .name("Test PostgreSQL")
                .description("Test veritabanı")
                .type(DataSourceType.POSTGRESQL)
                .connectionUrl("jdbc:postgresql://localhost:5432/test")
                .build();
    }

    @Test
    @DisplayName("Veri kaynağı başarıyla oluşturulmalı")
    void create_ShouldCreateDataSource_WhenValidRequest() {
        when(dataSourceRepository.existsByName("Test PostgreSQL")).thenReturn(false);
        when(dataSourceRepository.save(any(DataSource.class))).thenReturn(testDataSource);

        DataSourceResponse response = dataSourceService.create(testRequest);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Test PostgreSQL");
        verify(dataSourceRepository, times(1)).save(any(DataSource.class));
    }

    @Test
    @DisplayName("Var olan isimle veri kaynağı oluşturulamamalı")
    void create_ShouldThrowException_WhenNameExists() {
        when(dataSourceRepository.existsByName("Test PostgreSQL")).thenReturn(true);

        assertThatThrownBy(() -> dataSourceService.create(testRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("zaten mevcut");

        verify(dataSourceRepository, never()).save(any());
    }

    @Test
    @DisplayName("ID ile veri kaynağı getirilmeli")
    void getById_ShouldReturnDataSource_WhenExists() {
        when(dataSourceRepository.findById(1L)).thenReturn(Optional.of(testDataSource));

        DataSourceResponse response = dataSourceService.getById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Test PostgreSQL");
    }

    @Test
    @DisplayName("Olmayan ID ile veri kaynağı getirilememeli")
    void getById_ShouldThrowException_WhenNotExists() {
        when(dataSourceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dataSourceService.getById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Tüm veri kaynakları listelenebilmeli")
    void getAll_ShouldReturnAllDataSources() {
        when(dataSourceRepository.findAll()).thenReturn(List.of(testDataSource));

        List<DataSourceResponse> responses = dataSourceService.getAll();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getName()).isEqualTo("Test PostgreSQL");
    }

    @Test
    @DisplayName("Veri kaynağı başarıyla silinmeli")
    void delete_ShouldDeleteDataSource_WhenExists() {
        when(dataSourceRepository.findById(1L)).thenReturn(Optional.of(testDataSource));
        when(dataSourceRepository.save(any(DataSource.class))).thenReturn(testDataSource);

        dataSourceService.delete(1L);

        verify(dataSourceRepository, times(1)).save(argThat(ds ->
                !ds.getActive() && ds.getDeleted()));
    }
}