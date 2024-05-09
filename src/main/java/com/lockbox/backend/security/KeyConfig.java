package com.lockbox.backend.security;

import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to set up cryptographic keys.
 * This class provides a centralized way to configure and provide the RSA key used across the application,
 * particularly for JWT processing.
 */
@Configuration
public class KeyConfig {

    /**
     * Creates a bean that provides an RSAKey.
     * The RSAKey is generated using a utility class that abstracts the details of key generation.
     * This key will typically be used for signing and verifying JSON Web Tokens (JWTs).
     *
     * @return An RSAKey instance with both public and private key components.
     */
    @Bean
    public RSAKey rsaKey() {
        // Generates and returns an RSA key using the Jwks utility class.
        // The RSA key includes both a public and a private key, which are essential for
        // JWT signature verification and creation respectively.
        return Jwks.generateRsa();
    }
}