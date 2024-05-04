package com.lockbox.backend.controllers;

import com.lockbox.backend.models.MetaData;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.UUID;

import static com.lockbox.backend.repositories.FileRepository.getFileExtension;

@RestController
public class FileUploadController {

    MetaDataRepository metaDataRepository;
    public FileUploadController(MetaDataRepository metaDataRepository) {
        this.metaDataRepository = metaDataRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        // Check if file is empty
        if (file == null || file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please select a file to upload.");
        }

        try {
            String projectRoot = System.getProperty("user.dir");
            // Generate unique identifier
            String uniqueFileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();


            // Save metadata to the database
            MetaData metaData = new MetaData();
            metaData.setFileName(uniqueFileName);
            metaData.setLink("/uploads/" + uniqueFileName);
            metaData.setSize(file.getSize());
            metaData.setUploadDate(new Date());
            metaData.setExtension(getFileExtension(file.getOriginalFilename()));
            // Set uploaderId based on logged in user, assuming you have a way to get it
            metaData.setUploaderId(0); // Replace "userId" with actual user ID
            metaDataRepository.save(metaData);


            // Save the file
            file.transferTo(new File(projectRoot + "/uploads/" + File.separator + uniqueFileName));
            // Create access link
            String accessLink = "/uploads/" + uniqueFileName;
            return ResponseEntity.ok("File uploaded successfully. Access link: " + accessLink);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file");
        }
    }
}
