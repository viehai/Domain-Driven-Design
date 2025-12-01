package com.viehai.identity_service.identity.application.service;

import com.viehai.identity_service.identity.interfaces.dto.request.UserCreateRequest;
import com.viehai.identity_service.identity.interfaces.dto.request.UserUpdateRequest;
import com.viehai.identity_service.identity.interfaces.dto.response.AddressResponse;
import com.viehai.identity_service.identity.interfaces.dto.response.JobResponse;
import com.viehai.identity_service.identity.interfaces.dto.response.UserResponse;
import com.viehai.identity_service.identity.domain.model.Address;
import com.viehai.identity_service.identity.domain.model.User;
import com.viehai.identity_service.identity.domain.exception.AppException;
import com.viehai.identity_service.identity.domain.exception.ErrorCode;
import com.viehai.identity_service.identity.infrastructure.mapper.UserMapper;
import com.viehai.identity_service.identity.domain.repository.JobRepository;
import com.viehai.identity_service.identity.domain.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;

@Service
@Profile({"mysql","postgres"})
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    JobRepository jobRepository;
    UserRepository userRepository;
    UserMapper userMapper;

    @Transactional
    @CacheEvict(value = {"users","allUsers"}, allEntries = true)
    public UserResponse createUser(UserCreateRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTS);
        }

        User u = userMapper.toUser(req);
        u.setPassword(new BCryptPasswordEncoder(10).encode(req.getPassword()));


        if (req.getAddress() != null) {
            var ar = req.getAddress();
            Address a = new Address();
            a.setLine(ar.getLine().trim());
            a.setWard(ar.getWard() == null ? null : ar.getWard().trim());
            a.setCity(ar.getCity().trim());
            a.setCountry(ar.getCountry().trim());
            u.setAddress(a);
        }


        if (req.getJobIds() != null && !req.getJobIds().isEmpty()) {
            var jobs = jobRepository.findAllById(req.getJobIds());
            if (jobs.size() != req.getJobIds().size()) throw new AppException(ErrorCode.JOB_NOT_FOUND);
            u.setJobs(new LinkedHashSet<>(jobs));
        }

        return userMapper.toUserResponse(userRepository.save(u));
    }

    // READ (list) - cacheable
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    @Cacheable(value = "allUsers", key = "'all'")
    public List<UserResponse> getUsers() {
        System.out.println("Querying DB..."); // giữ để test cache như anh đang dùng
        // Lưu ý: nếu có method fetch relations (findAllWithRelations) thì nên dùng để tránh N+1
        return userRepository.findAll().stream()
                .map(u -> UserResponse.builder()
                        .id(u.getId())
                        .username(u.getUsername())
                        .firstName(u.getFirstName())
                        .lastName(u.getLastName())
                        .dob(u.getDob())
                        .jobs(u.getJobs().stream()
                                .map(j -> new JobResponse(j.getId(), j.getCode(), j.getName()))
                                .toList())
                        .address(u.getAddress() == null ? null :
                                new AddressResponse(
                                        u.getAddress().getId(),
                                        u.getAddress().getLine(),
                                        u.getAddress().getCity(),
                                        u.getAddress().getCountry()
                                ))
                        .build())
                .toList();
    }

    // READ (detail)
    @Transactional(readOnly = true)
    public UserResponse getUser(String id) {
        // Ưu tiên lấy kèm quan hệ nếu repo có method này
        var user = userRepository.findWithRelationsById(id)
                .orElseGet(() -> userRepository.findById(id)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)));
        return userMapper.toUserResponse(user);
    }

    // UPDATE
    @Transactional
    @CacheEvict(value = {"users","allUsers"}, allEntries = true)
    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        userMapper.updateUser(user, request); // chú ý cấu hình MapStruct bỏ qua null nếu cần

        return userMapper.toUserResponse(userRepository.save(user));
    }

    // Get my info
    @Transactional
    public UserResponse getMyInfo(){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserResponse(user);
    }

    // DELETE
    @Transactional
    @CacheEvict(value = {"users","allUsers"}, allEntries = true)
    public void deleteUser(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        userRepository.deleteById(userId);
    }
}
