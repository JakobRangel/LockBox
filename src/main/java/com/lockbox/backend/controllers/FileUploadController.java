package com.lockbox.backend.controllers;

import com.lockbox.backend.models.MetaData;
import com.lockbox.backend.repositories.FileEncryptorRepository;
import com.lockbox.backend.repositories.MetaDataRepository;
import com.lockbox.backend.repositories.UserRepository;
import com.lockbox.backend.security.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.lockbox.backend.repositories.FileRepository.getFileExtension;

@RestController
@CrossOrigin
public class FileUploadController {

    MetaDataRepository metaDataRepository;
    public FileUploadController(MetaDataRepository metaDataRepository) {
        this.metaDataRepository = metaDataRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFiles(@RequestParam("files") List<MultipartFile> files,
                                              @CookieValue(value = "userId", required = false) String userId,
                                              @RequestParam(value = "encryptionString", required = false) String encryptionString) {
        // Check if files are empty
        if (files == null || files.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please select at least one file to upload.");
        }

        try {
            String projectRoot = System.getProperty("user.dir");
            List<String> accessLinks = new ArrayList<>();

            for (MultipartFile file : files) {
                // Generate unique identifier
                String originalFileName = file.getOriginalFilename();
                String sanitizedFileName = originalFileName.replaceAll("\\s", "-"); // Replace each space with an dash

                String randomUUID = UUID.randomUUID().toString();





                // Save metadata to the database
                MetaData metaData = new MetaData();
                metaData.setUuid(randomUUID);
                metaData.setFileName(sanitizedFileName);
                metaData.setLink("/uploads/" + randomUUID);
                metaData.setSize(file.getSize());
                metaData.setUploadDate(new Date());
                metaData.setExtension(getFileExtension(sanitizedFileName));
                System.out.println("User: " + userId);

                if (userId != null) {
                    System.out.println("User logged in");
                    metaData.setUploaderId(Integer.parseInt(userId));
                } else {
                    metaData.setUploaderId(0);
                }
                if (encryptionString != null) {
                    metaData.setEncrypted(true);
                } else {
                    metaData.setEncrypted(false);
                }
                metaDataRepository.save(metaData);

                // Save the file
                File uploadDir = new File(projectRoot + "/uploads/");
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs(); // Create directory if it doesn't exist
                }

                File uploadedFile = new File(uploadDir, randomUUID);
                file.transferTo(uploadedFile);

                // If encryption string is provided, encrypt the file
                if (encryptionString != null && !encryptionString.isEmpty()) {
                    FileEncryptorRepository.encryptFile(uploadedFile, encryptionString);
                }

                // Create access link
                String accessLink = "/uploads/" + randomUUID;
                accessLinks.add(accessLink);
            }

            return ResponseEntity.ok(accessLinks.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload files");
        }
    }

}
