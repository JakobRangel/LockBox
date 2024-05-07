package com.lockbox.backend.repositories;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class FileEncryptorRepository {
    public static void encryptFile(File inputFile, String encryptionKey) throws Exception {
        // Read file content into a byte array
        byte[] fileContent = readFileContent(inputFile);

        // Encrypt the file content
        byte[] encryptedData = encrypt(fileContent, encryptionKey);

        // Write the encrypted data back to the file
        writeToFile(inputFile, encryptedData);
    }

    private static byte[] readFileContent(File file) throws IOException {
        try (InputStream inputStream = new FileInputStream(file)) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[8192];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            return buffer.toByteArray();
        }
    }

    private static void writeToFile(File file, byte[] data) throws FileNotFoundException {
        try (OutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] encrypt(byte[] data, String encryptionKey) throws Exception {
        SecretKey secretKey = generateKey(encryptionKey);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    public static SecretKey generateKey(String encryptionString) throws NoSuchAlgorithmException {
        // Use SHA-256 to hash the encryption string
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(encryptionString.getBytes());

        // Truncate the hash if it's longer than 256 bits (32 bytes)
        byte[] keyBytes = new byte[32];
        System.arraycopy(hash, 0, keyBytes, 0, Math.min(hash.length, keyBytes.length));

        // Create a SecretKeySpec from the hashed bytes
        return new SecretKeySpec(keyBytes, "AES");
    }

    public static void decryptFile(File inputFile, String encryptionKey, OutputStream outputStream) throws Exception {
        // Create SecretKeySpec object from the encryption key
        SecretKey secretKey = generateKey(encryptionKey);

        // Initialize Cipher for decryption
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        // Create input stream for reading from the encrypted file
        try (FileInputStream inputStream = new FileInputStream(inputFile);
             CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher)) {

            // Read encrypted data from input stream and write decrypted data to output stream
            byte[] buffer = new byte[8192]; // Adjust buffer size as needed
            int bytesRead;
            while ((bytesRead = cipherInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }
}
