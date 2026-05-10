package com.datasync.datasync.dto.request;

import com.datasync.datasync.domain.enums.SyncType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncJobRequest {

    @NotBlank(message = "İsim boş olamaz")
    @Size(max = 100)
    private String name;

    @NotNull(message = "Kaynak ID boş olamaz")
    private Long sourceId;

    @NotNull(message = "Hedef ID boş olamaz")
    private Long targetId;

    @NotNull(message = "Senkronizasyon türü boş olamaz")
    private SyncType type;

    private String cronExpression;
}