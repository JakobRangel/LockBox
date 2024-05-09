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

/**
 * Utility class for file encryption and decryption.
 * This class provides methods to encrypt and decrypt files using AES encryption algorithm.
 */
public class FileEncryptorRepository {
    /**
     * Encrypts the content of the specified file using the given encryption key.
     *
     * @param inputFile The file whose content will be encrypted.
     * @param encryptionKey The key used for encryption.
     * @throws Exception if there is an error during the encryption process.
     */
    public static void encryptFile(File inputFile, String encryptionKey) throws Exception {
        byte[] fileContent = readFileContent(inputFile);
        byte[] encryptedData = encrypt(fileContent, encryptionKey);
        writeToFile(inputFile, encryptedData);
    }

    /**
     * Reads the entire content of a file into a byte array.
     *
     * @param file The file to read.
     * @return A byte array containing the contents of the file.
     * @throws IOException if an I/O error occurs reading from the stream.
     */
    private static byte[] readFileContent(File file) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (InputStream inputStream = new FileInputStream(file)) {
            byte[] data = new byte[8192];
            int nRead;
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
        }
        return buffer.toByteArray();
    }

    /**
     * Writes the provided byte array to the specified file.
     *
     * @param file The file to write to.
     * @param data The data to write.
     * @throws FileNotFoundException if the file does not exist.
     * @throws IOException if an I/O error occurs writing to the file.
     */
    private static void writeToFile(File file, byte[] data) throws IOException {
        try (OutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(data);
        }
    }

    /**
     * Encrypts a byte array with the specified encryption key.
     *
     * @param data The data to encrypt.
     * @param encryptionKey The key used for encryption.
     * @return The encrypted byte array.
     * @throws Exception if encryption fails.
     */
    private static byte[] encrypt(byte[] data, String encryptionKey) throws Exception {
        SecretKey secretKey = generateKey(encryptionKey);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    /**
     * Generates a SecretKey based on the provided string.
     *
     * @param encryptionString The string from which the key is generated.
     * @return A SecretKey for AES encryption.
     * @throws NoSuchAlgorithmException if SHA-256 hashing is not available.
     */
    public static SecretKey generateKey(String encryptionString) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(encryptionString.getBytes(StandardCharsets.UTF_8));
        byte[] keyBytes = new byte[32];  // AES keys are typically 256 bits long
        System.arraycopy(hash, 0, keyBytes, 0, Math.min(hash.length, keyBytes.length));
        return new SecretKeySpec(keyBytes, "AES");
    }

    /**
     * Decrypts the content of the specified file and writes the decrypted data to an output stream.
     *
     * @param inputFile The file whose content will be decrypted.
     * @param encryptionKey The key used for decryption.
     * @param outputStream The stream to which the decrypted data will be written.
     * @throws Exception if decryption fails.
     */
    public static void decryptFile(File inputFile, String encryptionKey, OutputStream outputStream) throws Exception {
        SecretKey secretKey = generateKey(encryptionKey);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        try (CipherInputStream cipherInputStream = new CipherInputStream(new FileInputStream(inputFile), cipher)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = cipherInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }
}