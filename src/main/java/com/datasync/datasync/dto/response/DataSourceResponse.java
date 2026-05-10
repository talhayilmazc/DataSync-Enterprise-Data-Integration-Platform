package com.datasync.datasync.dto.response;

import com.datasync.datasync.domain.enums.DataSourceType;
import com.datasync.datasync.domain.enums.SyncStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataSourceResponse {
    private Long id;
    private String name;
    private String description;
    private DataSourceType type;
    private String connectionUrl;
    private SyncStatus status;
    private Boolean active;
    private Integer retryCount;
    private String lastErrorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}