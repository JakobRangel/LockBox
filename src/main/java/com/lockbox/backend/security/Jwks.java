package com.lockbox.backend.security;

import com.nimbusds.jose.jwk.RSAKey;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

/**
 * Utility class for handling JSON Web Key Set (JWKS) generation.
 * This class provides methods to generate RSA keys for use in JWT authentication mechanisms.
 */
public class Jwks {

    // Private constructor to prevent instantiation of this utility class
    private Jwks() {}

    /**
     * Generates an RSA key pair for signing and validating JWTs.
     * The method uses a key pair generator to create public and private RSA keys.
     *
     * @return RSAKey containing both the RSA public and private keys with a unique key identifier.
     */
    public static RSAKey generateRsa() {
        // Generate RSA key pair
        KeyPair keyPair = KeyGeneratorUtils.generateRsaKey();

        // Extract the public and private keys from the generated key pair
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        // Build and return an RSAKey which includes the public and private keys with a newly generated key ID
        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString()) // Assign a unique identifier for the key
                .build();
    }
}