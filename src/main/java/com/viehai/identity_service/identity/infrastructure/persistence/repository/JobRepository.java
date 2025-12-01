package com.viehai.identity_service.identity.domain.repository;

import com.viehai.identity_service.identity.domain.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {
    boolean existsByCode(String code);
}
