package com.lockbox.backend.controllers;

import com.lockbox.backend.models.MetaData;
import com.lockbox.backend.repositories.FileEncryptorRepository;
import com.lockbox.backend.repositories.MetaDataRepository;
import com.lockbox.backend.repositories.UserRepository;
import com.lockbox.backend.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Controller responsible for handling file uploads.
 */
@Controller
@CrossOrigin
public class FileUploadController {

    private final MetaDataRepository metaDataRepository;

    @Autowired
    public FileUploadController(MetaDataRepository metaDataRepository) {
        this.metaDataRepository = metaDataRepository;
    }

    /**
     * Endpoint to upload one or more files.
     *
     * @param files List of MultipartFile objects to upload.
     * @param userId Optional user ID from a cookie.
     * @param encryptionString Optional encryption key for encrypting the files.
     * @param isPrivate Flag to indicate if the files should be marked as private.
     * @return ResponseEntity with the result of the operation.
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFiles(@RequestParam("files") List<MultipartFile> files,
                                              @CookieValue(value = "userId", required = false) String userId,
                                              @RequestParam(value = "encryptionString", required = false) String encryptionString,
                                              @RequestParam(value = "private", required = false) boolean isPrivate) {
        if (files.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select at least one file to upload.");
        }

        List<String> accessLinks = new ArrayList<>();
        String projectRoot = System.getProperty("user.dir");
        File uploadDir = new File(projectRoot, "uploads");

        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        for (MultipartFile file : files) {
            String originalFileName = file.getOriginalFilename();
            String sanitizedFileName = originalFileName != null ? originalFileName.replaceAll("\\s", "-") : "default-name";
            String randomUUID = UUID.randomUUID().toString();
            MetaData metaData = createMetaData(randomUUID, sanitizedFileName, userId, encryptionString, isPrivate, file.getSize());

            try {
                File uploadedFile = new File(uploadDir, randomUUID);
                file.transferTo(uploadedFile);
                if (encryptionString != null && !encryptionString.isBlank()) {
                    FileEncryptorRepository.encryptFile(uploadedFile, encryptionString);
                }
                accessLinks.add("/uploads/" + randomUUID);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file: " + originalFileName);
            }

            metaDataRepository.save(metaData);
        }

        return ResponseEntity.ok(String.join(", ", accessLinks));
    }

    /**
     * Helper method to create and return a MetaData instance.
     */
    private MetaData createMetaData(String uuid, String fileName, String userId, String encryptionString, boolean isPrivate, long fileSize) {
        MetaData metaData = new MetaData();
        metaData.setUuid(uuid);
        metaData.setFileName(fileName);
        metaData.setLink("/uploads/" + uuid);
        metaData.setSize(fileSize);
        metaData.setUploadDate(new Date());
        metaData.setExtension(getFileExtension(fileName));
        metaData.setUploaderId(userId != null ? Integer.parseInt(userId) : 0);
        metaData.setEncrypted(encryptionString != null);
        metaData.setPrivate(isPrivate);
        return metaData;
    }

    /**
     * Extracts the file extension from the file name.
     */
    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}