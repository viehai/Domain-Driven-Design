package com.viehai.identity_service.identity.interfaces.rest;

import com.viehai.identity_service.identity.interfaces.dto.response.ApiResponse;
import com.viehai.identity_service.identity.interfaces.dto.request.JobCreateRequest;
import com.viehai.identity_service.identity.interfaces.dto.response.JobResponse;
import com.viehai.identity_service.identity.application.service.JobService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobs")
@Profile({"mysql","postgres"})
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JobController {

    JobService jobService;

    @PostMapping
    public ResponseEntity<ApiResponse<JobResponse>> create(@Valid @RequestBody JobCreateRequest req) {
        JobResponse data = jobService.create(req);
        ApiResponse<JobResponse> res = new ApiResponse<>();
        res.setResult(data);
        return ResponseEntity.ok(res);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<JobResponse>>> list() {
        List<JobResponse> data = jobService.findAll();
        ApiResponse<List<JobResponse>> res = new ApiResponse<>();
        res.setResult(data);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobResponse>> getById(@PathVariable Long id) {
        JobResponse data = jobService.getById(id);
        ApiResponse<JobResponse> res = new ApiResponse<>();
        res.setResult(data);
        return ResponseEntity.ok(res);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<JobResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody JobCreateRequest req // có thể tách JobUpdateRequest nếu muốn
    ) {
        JobResponse data = jobService.update(id, req);
        ApiResponse<JobResponse> res = new ApiResponse<>();
        res.setResult(data);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        jobService.delete(id);
        ApiResponse<Void> res = new ApiResponse<>();
        return ResponseEntity.ok(res); // hoặc noContent()
    }
}
