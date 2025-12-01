package com.viehai.identity_service.identity.application.service;

import com.viehai.identity_service.identity.interfaces.dto.request.AuthenticationRequest;
import com.viehai.identity_service.identity.interfaces.dto.response.AuthenticationResponse;
import com.viehai.identity_service.identity.domain.exception.AppException;
import com.viehai.identity_service.identity.domain.exception.ErrorCode;
import com.viehai.identity_service.identity.domain.repository.UserRepository;
import com.viehai.identity_service.identity.infrastructure.security.JwtService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder; // inject từ SecurityConfig
    JwtService jwtService;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        String token = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .authenticated(true)
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .build();
    }
}

