package com.viehai.identity_service.identity.interfaces.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreateRequest {

    @Size(min = 4, message = "USERNAME_INVALID")
    String username;

    @Size(min = 8, message = "PASSWORD_INVALID")
    String password;

    @Email(message = "EMAIL_INVALID")
    String email;

    @NotBlank @Size(max = 100)
    String firstName;

    @NotBlank
    @Size(max = 100)
    String lastName;

    @NotNull
    LocalDate dob;     // dạng "YYYY-MM-DD"


    @Valid
    AddressRequest address;
    List<Long> jobIds;

}
