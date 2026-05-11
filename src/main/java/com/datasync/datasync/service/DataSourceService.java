package com.datasync.datasync.service;

import com.datasync.datasync.dto.request.DataSourceRequest;
import com.datasync.datasync.dto.response.DataSourceResponse;
import com.datasync.datasync.dto.response.MessageResponse;

import java.util.List;

public interface DataSourceService {
    DataSourceResponse create(DataSourceRequest request);
    DataSourceResponse getById(Long id);
    DataSourceResponse getByName(String name);
    List<DataSourceResponse> getAll();
    List<DataSourceResponse> getAllActive();
    DataSourceResponse update(Long id, DataSourceRequest request);
    MessageResponse delete(Long id);
    MessageResponse testConnection(Long id);
    MessageResponse activate(Long id);
    MessageResponse deactivate(Long id);
}