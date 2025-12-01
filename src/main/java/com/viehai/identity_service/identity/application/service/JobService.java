package com.viehai.identity_service.identity.application.service;

import com.viehai.identity_service.identity.interfaces.dto.request.JobCreateRequest;
import com.viehai.identity_service.identity.interfaces.dto.response.JobResponse;
import com.viehai.identity_service.identity.domain.model.Job;
import com.viehai.identity_service.identity.domain.model.User;
import com.viehai.identity_service.identity.domain.exception.AppException;
import com.viehai.identity_service.identity.domain.exception.ErrorCode;
import com.viehai.identity_service.identity.domain.repository.JobRepository;
import com.viehai.identity_service.identity.domain.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JobService {

    JobRepository jobRepository;
    UserRepository userRepository;

    // CREATE
    @Transactional
    @CacheEvict(value = { "users", "allUsers" }, allEntries = true)
    public JobResponse create(JobCreateRequest req) {
        if (jobRepository.existsByCode(req.getCode())) {
            throw new AppException(ErrorCode.JOB_CODE_EXISTS);
        }
        Job job = new Job();
        job.setCode(req.getCode().trim());
        job.setName(req.getName().trim());
        return toResponse(jobRepository.save(job));
    }

    // READ - list
    @Transactional(readOnly = true)
    public List<JobResponse> findAll() {
        return jobRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    // READ - detail
    @Transactional(readOnly = true)
    public JobResponse getById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));
        return toResponse(job);
    }

    // UPDATE
    @Transactional
    @CacheEvict(value = { "users", "allUsers" }, allEntries = true)
    public JobResponse update(Long id, JobCreateRequest req) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));

        // Nếu code đổi, check unique
        String newCode = req.getCode().trim();
        if (!newCode.equals(job.getCode()) && jobRepository.existsByCode(newCode)) {
            throw new AppException(ErrorCode.JOB_CODE_EXISTS);
        }

        job.setCode(newCode);
        job.setName(req.getName().trim());
        return toResponse(jobRepository.save(job));
    }

    // DELETE (dọn quan hệ n–n trước khi xoá)
    @Transactional
    @CacheEvict(value = { "users", "allUsers" }, allEntries = true)
    public void delete(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));

        // Dọn association ở owning side để tránh lỗi FK
        // Cần method sau trong UserRepository: List<User> findAllByJobs_Id(Long jobId);
        List<User> usersHavingThisJob = userRepository.findAllByJobs_Id(id);
        for (User u : usersHavingThisJob) {
            u.getJobs().remove(job);
        }

        jobRepository.delete(job);
    }

    // --- helper ---
    private JobResponse toResponse(Job j) {
        return new JobResponse(j.getId(), j.getCode(), j.getName());
    }
}
