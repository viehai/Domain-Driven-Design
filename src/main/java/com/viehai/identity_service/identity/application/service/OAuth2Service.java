package com.viehai.identity_service.identity.application.service;

import com.viehai.identity_service.identity.interfaces.dto.response.AuthenticationResponse;
import com.viehai.identity_service.identity.domain.model.User;
import com.viehai.identity_service.identity.domain.enums.Role;
import com.viehai.identity_service.identity.domain.repository.UserRepository;
import com.viehai.identity_service.identity.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class OAuth2Service {
    
    UserRepository userRepository;
    JwtService jwtService;
    
    public AuthenticationResponse processOAuth2User(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        
        // Check if user exists by email
        Optional<User> existingUser = userRepository.findByEmail(email);
        
        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
            if (user.getUsername() == null || user.getUsername().isEmpty()) {
                user.setUsername(email);
            }
            // Keep existing role
            user = userRepository.save(user);
        } else {
            User newUser = User.builder()
                .email(email)
                .username(email)
                .firstName(extractFirstName(name))
                .lastName(extractLastName(name))
                .password("")
                .role(Role.USER) // Set default role as USER for new Google users
                .build();
            
            user = userRepository.save(newUser);
        }
        
        String token = jwtService.generateToken(user);
        
        return AuthenticationResponse.builder()
                .authenticated(true)
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .build();
    }
    
    private String extractFirstName(String fullName) {
        if (fullName == null || fullName.isEmpty()) {
            return "";
        }
        String[] nameParts = fullName.trim().split("\\s+");
        return nameParts.length > 0 ? nameParts[nameParts.length - 1] : "";
    }
    
    private String extractLastName(String fullName) {
        if (fullName == null || fullName.isEmpty()) {
            return "";
        }
        String[] nameParts = fullName.trim().split("\\s+");
        if (nameParts.length > 1) {
            return String.join(" ", java.util.Arrays.copyOf(nameParts, nameParts.length - 1));
        }
        return "";
    }
}
