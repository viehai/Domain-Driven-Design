package com.viehai.identity_service.identity.infrastructure.bootstrap;

import com.viehai.identity_service.identity.domain.model.Job;
import com.viehai.identity_service.identity.domain.model.User;
import com.viehai.identity_service.identity.domain.enums.Role;
import com.viehai.identity_service.identity.domain.repository.JobRepository;
import com.viehai.identity_service.identity.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import jakarta.transaction.Transactional;  // hoặc: org.springframework.transaction.annotation.Transactional
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
/** chạy seeder khi active profile là 'dev' HOẶC 'mysql' */
@Profile({"dev","mysql"})
public class DevDataSeeder implements ApplicationRunner {

    JobRepository jobRepository;

    UserRepository userRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedJobs();
        seedAdminUser();
    }

    private void seedJobs() {
        if (jobRepository.count() == 0) {
            jobRepository.saveAll(List.of(
                    Job.builder().code("SE").name("Software Engineer").build(),
                    Job.builder().code("PM").name("Product Manager").build()
            ));
            log.info("Seeded jobs: SE, PM");
        } else {
            log.info("Jobs already present: {}", jobRepository.count());
        }
    }

    private void seedAdminUser() {
        String adminEmail = "admin@example.com";
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = User.builder()
                    .email(adminEmail)
                    .username("admin")
                    .password("$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG")
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            log.info("Created admin user: {}", adminEmail);
        }
    }
}
