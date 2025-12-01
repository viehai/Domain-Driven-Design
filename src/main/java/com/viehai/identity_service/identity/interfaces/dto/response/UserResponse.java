package com.viehai.identity_service.identity.interfaces.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;
    String username;
    String email;
    String firstName;
    String lastName;
    LocalDate dob;
    List<JobResponse> jobs;
    AddressResponse address;
}
