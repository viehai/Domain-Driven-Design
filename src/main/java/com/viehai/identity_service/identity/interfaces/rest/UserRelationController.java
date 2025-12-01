package com.viehai.identity_service.identity.interfaces.rest;

import com.viehai.identity_service.identity.interfaces.dto.request.AddressRequest;
import com.viehai.identity_service.identity.interfaces.dto.request.UserJobsReplaceRequest;
import com.viehai.identity_service.identity.interfaces.dto.response.AddressResponse;
import com.viehai.identity_service.identity.interfaces.dto.response.JobResponse;
import com.viehai.identity_service.identity.application.service.UserRelationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Profile({"mysql","postgres"})
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserRelationController {

    UserRelationService service;

    // Replace toàn bộ jobs
    @PutMapping("/{id}/jobs")
    public ResponseEntity<Void> replaceJobs(@PathVariable String id,
                                            @RequestBody UserJobsReplaceRequest req) {
        service.replaceJobs(id, req.getJobIds());
        return ResponseEntity.noContent().build();
    }

    // Xoá 1 job khỏi user
    @DeleteMapping("/{id}/jobs/{jobId}")
    public ResponseEntity<Void> removeJob(@PathVariable("id") String id, @PathVariable("jobId") Long jobId) {
        service.removeJob(id, jobId);
        return ResponseEntity.noContent().build();
    }

    // Lấy danh sách jobs của user
    @GetMapping("/{id}/jobs")
    public ResponseEntity<List<JobResponse>> getJobs(@PathVariable("id") String id) {
        return ResponseEntity.ok(service.getJobs(id));
    }

    // Upsert địa chỉ 1–1
    @PutMapping("/{id}/address")
    public ResponseEntity<AddressResponse> upsertAddress(@PathVariable("id") String id, @Valid @RequestBody AddressRequest req) {
        return ResponseEntity.ok(service.upsertAddress(id, req));
    }

    // Lấy địa chỉ hiện tại
    @GetMapping("/{id}/address")
    public ResponseEntity<AddressResponse> getAddress(@PathVariable("id") String id) {
        return ResponseEntity.ok(service.getAddress(id));
    }
}
