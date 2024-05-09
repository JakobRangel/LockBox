package com.lockbox.backend.security;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class to handle generation of cryptographic key pairs.
 * Specifically, this class provides methods to generate RSA key pairs which
 * are commonly used in digital signatures and data encryption.
 */
public class KeyGeneratorUtils {

    // Default RSA key size in bits. 2048 bits is currently considered secure for most purposes
    // and provides a good balance between security and performance.
    private static final int DEFAULT_KEY_SIZE = 2048;

    // Private constructor to prevent instantiation of the utility class.
    private KeyGeneratorUtils() {}

    /**
     * Generates an RSA KeyPair with a default key size.
     * This method encapsulates the RSA key generation process, abstracting
     * the complexities of Java's security APIs from the caller.
     *
     * @return A KeyPair containing both public and private RSA keys.
     * @throws SecurityException if RSA algorithm is not available or if key generation fails.
     */
    public static KeyPair generateRsaKey() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(DEFAULT_KEY_SIZE);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException ex) {
            // Rethrow as a unchecked exception to avoid the need for callers to handle it,
            // as this is an unlikely error and indicates a serious configuration issue if it occurs.
            throw new SecurityException("RSA algorithm not available", ex);
        }
    }
}
