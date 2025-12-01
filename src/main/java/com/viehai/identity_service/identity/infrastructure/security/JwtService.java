package com.viehai.identity_service.identity.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.viehai.identity_service.identity.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private static final String USER_ID_CLAIM = "userId";
    private static final String USERNAME_CLAIM = "username";
    private static final String EMAIL_CLAIM = "email";
    private static final String ROLE_CLAIM = "role";

    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return JWT.create()
                .withSubject(user.getId())
                .withClaim(USER_ID_CLAIM, user.getId())
                .withClaim(USERNAME_CLAIM, user.getUsername())
                .withClaim(EMAIL_CLAIM, user.getEmail())
                .withClaim(ROLE_CLAIM, user.getRole().name())
                .withIssuedAt(now)
                .withExpiresAt(expiryDate)
                .sign(Algorithm.HMAC256(secret));
    }

    public boolean verifyToken(String token) {
        try {
            JWT.require(Algorithm.HMAC256(secret))
                    .build()
                    .verify(token);
            return true;
        } catch (JWTVerificationException e) {
            log.error("JWT verification failed: {}", e.getMessage());
            return false;
        }
    }

    public String extractUserId(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            return decodedJWT.getClaim(USER_ID_CLAIM).asString();
        } catch (Exception e) {
            log.error("Failed to extract user ID from token: {}", e.getMessage());
            return null;
        }
    }

    public String extractUsername(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            return decodedJWT.getClaim(USERNAME_CLAIM).asString();
        } catch (Exception e) {
            log.error("Failed to extract username from token: {}", e.getMessage());
            return null;
        }
    }

    public String extractEmail(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            return decodedJWT.getClaim(EMAIL_CLAIM).asString();
        } catch (Exception e) {
            log.error("Failed to extract email from token: {}", e.getMessage());
            return null;
        }
    }

    public String extractClaim(String token, String claimName) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            return decodedJWT.getClaim(claimName).asString();
        } catch (Exception e) {
            log.error("Failed to extract claim {} from token: {}", claimName, e.getMessage());
            return null;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            return decodedJWT.getExpiresAt().before(new Date());
        } catch (Exception e) {
            log.error("Failed to check token expiration: {}", e.getMessage());
            return true;
        }
    }
}
