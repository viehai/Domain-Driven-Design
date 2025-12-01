package com.viehai.identity_service.identity.domain.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "users")
public class UserMongo implements Serializable {
    @Id
    String id;

    String username;
    String password;
    String email;
    String firstName;
    String lastName;
    LocalDate dob;
}
