package com.lockbox.backend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class responsible for generating JSON Web Tokens (JWTs).
 * This class encapsulates all JWT creation logic, ensuring that the tokens are
 * generated consistently and correctly throughout the application.
 */
@Service
public class TokenService {
    private final JwtEncoder encoder;

    @Value("${app.token.duration.hours:1}")
    private long tokenValidityDuration;

    public TokenService(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    /**
     * Generates a JWT for a given authentication object.
     * The token includes claims such as issuer, issued at, expiration time,
     * subject (username), scopes (authorities), and a unique token ID.
     *
     * @param authentication The authentication object containing the principal and authorities.
     * @return A string representation of the JWT.
     */
    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();

        // Collecting authorities to a space-separated string to represent scopes in the token.
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        // Building the JWT claims set.
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("LockBox")
                .issuedAt(now)
                .expiresAt(now.plus(tokenValidityDuration, ChronoUnit.HOURS))
                .subject(authentication.getName()) // Username or user identifier
                .claim("scope", scope) // User authorities
                .claim("tokenId", UUID.randomUUID().toString()) // Unique token identifier
                .build();

        // Encoding the JWT and returning its string representation.
        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}