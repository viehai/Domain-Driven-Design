package com.viehai.identity_service.identity.interfaces.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserJobsReplaceRequest {
    List<Long> jobIds;
}
