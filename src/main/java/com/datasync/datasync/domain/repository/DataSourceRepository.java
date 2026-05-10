package com.datasync.datasync.domain.repository;

import com.datasync.datasync.domain.entity.DataSource;
import com.datasync.datasync.domain.enums.DataSourceType;
import com.datasync.datasync.domain.enums.SyncStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DataSourceRepository extends JpaRepository<DataSource, Long> {
    Optional<DataSource> findByName(String name);
    List<DataSource> findByType(DataSourceType type);
    List<DataSource> findByStatus(SyncStatus status);
    List<DataSource> findByActiveTrue();
    boolean existsByName(String name);
}