package com.datasync.datasync.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncJobTriggerRequest {

    @NotBlank(message = "Job ID boş olamaz")
    private String jobId;

    private String triggeredBy;
}