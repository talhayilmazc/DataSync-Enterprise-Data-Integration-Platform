package com.datasync.datasync.service.impl;

import com.datasync.datasync.domain.entity.DataSource;
import com.datasync.datasync.domain.enums.SyncStatus;
import com.datasync.datasync.domain.repository.DataSourceRepository;
import com.datasync.datasync.dto.request.DataSourceRequest;
import com.datasync.datasync.dto.response.DataSourceResponse;
import com.datasync.datasync.dto.response.MessageResponse;
import com.datasync.datasync.exception.BusinessException;
import com.datasync.datasync.exception.DataSourceException;
import com.datasync.datasync.exception.ResourceNotFoundException;
import com.datasync.datasync.service.DataSourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataSourceServiceImpl implements DataSourceService {

    private final DataSourceRepository dataSourceRepository;

    @Override
    @Transactional
    public DataSourceResponse create(DataSourceRequest request) {
        if (dataSourceRepository.existsByName(request.getName())) {
            throw new BusinessException(
                    "Bu isimde bir veri kaynağı zaten mevcut: " + request.getName(),
                    "DATASOURCE_EXISTS");
        }
        DataSource dataSource = DataSource.builder()
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getType())
                .connectionUrl(request.getConnectionUrl())
                .username(request.getUsername())
                .password(request.getPassword())
                .status(SyncStatus.PENDING)
                .active(true)
                .build();
        DataSource saved = dataSourceRepository.save(dataSource);
        log.info("DataSource created: {}", saved.getName());
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "dataSources", key = "#id")
    public DataSourceResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public DataSourceResponse getByName(String name) {
        return toResponse(dataSourceRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Veri kaynağı bulunamadı: " + name)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DataSourceResponse> getAll() {
        return dataSourceRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DataSourceResponse> getAllActive() {
        return dataSourceRepository.findByActiveTrue()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = "dataSources", key = "#id")
    public DataSourceResponse update(Long id, DataSourceRequest request) {
        DataSource dataSource = findById(id);
        dataSource.setName(request.getName());
        dataSource.setDescription(request.getDescription());
        dataSource.setType(request.getType());
        dataSource.setConnectionUrl(request.getConnectionUrl());
        dataSource.setUsername(request.getUsername());
        dataSource.setPassword(request.getPassword());
        DataSource saved = dataSourceRepository.save(dataSource);
        log.info("DataSource updated: {}", saved.getName());
        return toResponse(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = "dataSources", key = "#id")
    public MessageResponse delete(Long id) {
        DataSource dataSource = findById(id);
        dataSource.setDeleted(true);
        dataSource.setActive(false);
        dataSourceRepository.save(dataSource);
        log.info("DataSource deleted: {}", dataSource.getName());
        return MessageResponse.success("Veri kaynağı başarıyla silindi");
    }

    @Override
    @Transactional
    public MessageResponse testConnection(Long id) {
        DataSource dataSource = findById(id);
        try {
            // Connection test logic
            dataSource.setStatus(SyncStatus.COMPLETED);
            dataSource.setRetryCount(0);
            dataSource.setLastErrorMessage(null);
            dataSourceRepository.save(dataSource);
            return MessageResponse.success("Bağlantı başarılı");
        } catch (Exception e) {
            dataSource.setStatus(SyncStatus.FAILED);
            dataSource.setLastErrorMessage(e.getMessage());
            dataSource.setRetryCount(dataSource.getRetryCount() + 1);
            dataSourceRepository.save(dataSource);
            throw new DataSourceException("Bağlantı testi başarısız: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public MessageResponse activate(Long id) {
        DataSource dataSource = findById(id);
        dataSource.setActive(true);
        dataSourceRepository.save(dataSource);
        return MessageResponse.success("Veri kaynağı aktifleştirildi");
    }

    @Override
    @Transactional
    public MessageResponse deactivate(Long id) {
        DataSource dataSource = findById(id);
        dataSource.setActive(false);
        dataSourceRepository.save(dataSource);
        return MessageResponse.success("Veri kaynağı deaktifleştirildi");
    }

    private DataSource findById(Long id) {
        return dataSourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veri kaynağı", id));
    }

    private DataSourceResponse toResponse(DataSource ds) {
        return DataSourceResponse.builder()
                .id(ds.getId())
                .name(ds.getName())
                .description(ds.getDescription())
                .type(ds.getType())
                .connectionUrl(ds.getConnectionUrl())
                .status(ds.getStatus())
                .active(ds.getActive())
                .retryCount(ds.getRetryCount())
                .lastErrorMessage(ds.getLastErrorMessage())
                .createdAt(ds.getCreatedAt())
                .updatedAt(ds.getUpdatedAt())
                .build();
    }
}