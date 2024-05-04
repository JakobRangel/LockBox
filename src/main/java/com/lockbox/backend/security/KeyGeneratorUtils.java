package com.lockbox.backend.security;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class KeyGeneratorUtils {
    private static final int DEFAULT_KEY_SIZE = 2048; // Default to 2048, optionally could change later for increased security, but at this time 2048

    private KeyGeneratorUtils() {}

    public static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(DEFAULT_KEY_SIZE);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException ex) {
            throw new SecurityException("RSA algorithm not available", ex);
        }
        return keyPair;
    }
}
