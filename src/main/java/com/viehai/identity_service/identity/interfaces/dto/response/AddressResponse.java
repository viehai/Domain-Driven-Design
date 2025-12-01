package com.viehai.identity_service.identity.interfaces.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressResponse {
    Long id;
    String line;
    String city;
    String country;
}
