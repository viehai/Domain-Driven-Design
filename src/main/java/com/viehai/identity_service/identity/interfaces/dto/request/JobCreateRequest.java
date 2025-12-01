package com.viehai.identity_service.identity.interfaces.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data @NoArgsConstructor @AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobCreateRequest {
    @NotBlank(message = "JOB_CODE_REQUIRED")
    @Size(max = 50, message = "JOB_CODE_TOO_LONG")
    private String code;

    @NotBlank(message = "JOB_NAME_REQUIRED")
    @Size(max = 100, message = "JOB_NAME_TOO_LONG")
    private String name;
}
