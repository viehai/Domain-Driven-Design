package com.viehai.identity_service.identity.domain.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import com.viehai.identity_service.identity.domain.enums.Role;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "users")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36) //
    String id;

    @Column(nullable = false, unique = true, length = 100)
    String username;

    @Column(nullable = false)
    String password;

    @Column(unique = true, length = 255)
    String email;

    String firstName;
    String lastName;
    LocalDate dob;

    // n–n: users ↔ jobs (bảng nối user_jobs)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_jobs", joinColumns = @JoinColumn(name = "user_id"), // sẽ là VARCHAR(36)
            inverseJoinColumns = @JoinColumn(name = "job_id") // sẽ là BIGINT
    )
    @Builder.Default
    private Set<Job> jobs = new LinkedHashSet<>();

    // 1–1: users.address_id -> addresses.id
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id", unique = true)
    Address address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    Role role = Role.USER; // Default role is USER
}
