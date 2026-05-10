package com.datasync.datasync.domain.entity;

import com.datasync.datasync.domain.enums.DataSourceType;
import com.datasync.datasync.domain.enums.SyncStatus;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "data_sources")
public class DataSource extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, length = 200)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DataSourceType type;

    @Column(nullable = false, length = 500)
    private String connectionUrl;

    @Column(length = 100)
    private String username;

    @Column(length = 200)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SyncStatus status;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(nullable = false)
    @Builder.Default
    private Integer retryCount = 0;

    @Column(length = 500)
    private String lastErrorMessage;
}