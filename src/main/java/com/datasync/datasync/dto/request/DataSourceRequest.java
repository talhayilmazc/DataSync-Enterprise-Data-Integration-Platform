package com.datasync.datasync.dto.request;

import com.datasync.datasync.domain.enums.DataSourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataSourceRequest {

    @NotBlank(message = "İsim boş olamaz")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Açıklama boş olamaz")
    @Size(max = 200)
    private String description;

    @NotNull(message = "Tür boş olamaz")
    private DataSourceType type;

    @NotBlank(message = "Bağlantı URL boş olamaz")
    @Size(max = 500)
    private String connectionUrl;

    private String username;
    private String password;
}