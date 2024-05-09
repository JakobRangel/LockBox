package com.lockbox.backend.controllers;

import com.lockbox.backend.models.MetaData;
import com.lockbox.backend.repositories.FileEncryptorRepository;
import com.lockbox.backend.repositories.MetaDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * Controller responsible for handling file download requests.
 */
@Controller
@CrossOrigin
public class FileDownloadController {
    @Autowired
    private MetaDataRepository metaDataRepository;

    /**
     * Endpoint to download files by UUID, potentially decrypting them based on request parameters.
     *
     * @param uuid The UUID of the file to download.
     * @param encryptionString The encryption key for decrypting the file, if necessary.
     * @return The file as a downloadable resource or appropriate HTTP response.
     */
    @GetMapping("/uploads/{uuid:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String uuid,
                                                 @RequestParam(value = "encryptionString", required = false) String encryptionString) throws Exception {
        String projectRoot = System.getProperty("user.dir");
        Path filePath = Paths.get(projectRoot, "uploads", uuid);

        MetaData metaData = metaDataRepository.findByUuid(uuid);
        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }

        if (metaData.isEncrypted()) {
            return handleEncryptedFile(filePath, encryptionString, metaData);
        } else {
            return handleUnencryptedFile(filePath, metaData);
        }
    }

    /**
     * Handles the decryption and downloading of encrypted files.
     *
     * @param filePath The path to the encrypted file.
     * @param encryptionString The decryption key provided by the client.
     * @param metaData MetaData containing file details.
     * @return ResponseEntity containing the decrypted file or an error.
     */
    private ResponseEntity<Resource> handleEncryptedFile(Path filePath, String encryptionString, MetaData metaData) throws Exception {
        if (encryptionString == null) {
            HttpHeaders header = new HttpHeaders();
            header.add("Error-Message", "This file is encrypted. Please submit encryptionString in request");
            return ResponseEntity.badRequest().headers(header).build();
        }
        ByteArrayOutputStream decryptedOutputStream = new ByteArrayOutputStream();
        FileEncryptorRepository.decryptFile(filePath.toFile(), encryptionString, decryptedOutputStream);
        ByteArrayResource resource = new ByteArrayResource(decryptedOutputStream.toByteArray());
        return buildResponseEntity(resource, metaData.getFileName());
    }

    /**
     * Handles the downloading of unencrypted files.
     *
     * @param filePath The path to the file.
     * @param metaData MetaData containing file details.
     * @return ResponseEntity containing the file or an error.
     */
    private ResponseEntity<Resource> handleUnencryptedFile(Path filePath, MetaData metaData) throws Exception {
        Resource resource = new UrlResource(filePath.toUri());
        if (resource.exists() && resource.isReadable()) {
            return buildResponseEntity(resource, metaData.getFileName());
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Builds the ResponseEntity with proper headers for file download.
     *
     * @param resource The file resource to download.
     * @param fileName The name of the file to set in the header.
     * @return ResponseEntity with headers set for downloading the file.
     */
    private ResponseEntity<Resource> buildResponseEntity(Resource resource, String fileName) {
        String contentType = determineContentType(fileName);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
        return ResponseEntity.ok().headers(headers).body(resource);
    }

    /**
     * Determines the content type of a file based on its extension.
     *
     * @param fileName The name of the file.
     * @return The content type string.
     */
    private String determineContentType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        switch (extension) {
            case "jpg":
            case "jpeg":
                return MediaType.IMAGE_JPEG_VALUE;
            case "png":
                return MediaType.IMAGE_PNG_VALUE;
            case "gif":
                return MediaType.IMAGE_GIF_VALUE;
            case "txt":
                return MediaType.TEXT_PLAIN_VALUE;
            case "html":
            case "htm":
                return MediaType.TEXT_HTML_VALUE;
            case "json":
                return MediaType.APPLICATION_JSON_VALUE;
            case "pdf":
                return MediaType.APPLICATION_PDF_VALUE;
            case "xml":
                return MediaType.APPLICATION_XML_VALUE;
            case "zip":
                return "application/zip";
            case "tar":
                return "application/x-tar";
            case "mp4":
                return "video/mp4";
            case "mp3":
                return "audio/mpeg";
            // Additional file types can be added here
            default:
                return MediaType.APPLICATION_OCTET_STREAM_VALUE; // Fallback for unknown types
        }
    }

}