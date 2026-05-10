package com.datasync.datasync.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncStatsResponse {
    private Long totalJobs;
    private Long activeJobs;
    private Long completedJobs;
    private Long failedJobs;
    private Long totalRecordsProcessed;
    private Long totalDataSources;
    private Long activeDataSources;
}