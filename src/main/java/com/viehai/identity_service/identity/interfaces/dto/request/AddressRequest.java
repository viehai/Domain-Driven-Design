package com.viehai.identity_service.identity.interfaces.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressRequest {
    @NotBlank @Size(max = 255)
    String line;

    @Size(max = 100)
    String ward;

    @NotBlank @Size(max = 100)
    String city;

    @NotBlank @Size(max = 100)
    String country;

}
