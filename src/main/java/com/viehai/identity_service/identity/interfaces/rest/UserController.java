package com.viehai.identity_service.identity.interfaces.rest;

import com.viehai.identity_service.identity.interfaces.dto.request.UserCreateRequest;
import com.viehai.identity_service.identity.interfaces.dto.request.UserUpdateRequest;
import com.viehai.identity_service.identity.interfaces.dto.response.ApiResponse;
import com.viehai.identity_service.identity.interfaces.dto.response.UserResponse;
import com.viehai.identity_service.identity.application.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@Profile({"mysql","postgres"})
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserResponse data = userService.createUser(request); // service trả UserResponse (không phải Entity)
        ApiResponse<UserResponse> res = new ApiResponse<>();
        res.setResult(data);
        return ResponseEntity.created(URI.create("/users/" + data.getId())).body(res);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> listUsers() {
        List<UserResponse> data = userService.getUsers();
        ApiResponse<List<UserResponse>> res = new ApiResponse<>();
        res.setResult(data);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/myInfo")
    public ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable String userId) {
        UserResponse data = userService.getUser(userId);
        ApiResponse<UserResponse> res = new ApiResponse<>();
        res.setResult(data);
        return ResponseEntity.ok(res);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable String userId,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        UserResponse data = userService.updateUser(userId, request);
        ApiResponse<UserResponse> res = new ApiResponse<>();
        res.setResult(data);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
