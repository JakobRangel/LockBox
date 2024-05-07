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

@Controller
@CrossOrigin
public class FileDownloadController {
    @Autowired
    private MetaDataRepository metaDataRepository;

    @GetMapping("/uploads/{uuid:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String uuid,
                                                 @RequestParam(value = "encryptionString", required = false) String encryptionString) throws Exception {


        // Get the root directory of the project
        String projectRoot = System.getProperty("user.dir");

        MetaData metaData = metaDataRepository.getMetaDataByUUID(uuid);
        Path path = Paths.get(projectRoot, "/uploads/", uuid);
        // Check if the file exists
        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }


        if(metaData.isEncrypted()) {
            if(encryptionString == null) {
                return ResponseEntity.badRequest().build();
            }
            // Resolve the encrypted file path
            Path encryptedFilePath = path;
            ByteArrayOutputStream decryptedOutputStream = new ByteArrayOutputStream();
            FileEncryptorRepository.decryptFile(encryptedFilePath.toFile(), encryptionString, decryptedOutputStream);
            ByteArrayResource resource = new ByteArrayResource(decryptedOutputStream.toByteArray());
            // Determine the content type based on the file extension
            String contentType = determineContentType(metaData.getFileName());

            // Prepare the HTTP headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + metaData.getFileName());

            // Return the decrypted file as a ResponseEntity
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } else {
            // Resolve the file path
            Path filePath = path;
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                // Determine the content type based on the file extension
                String contentType = determineContentType(metaData.getFileName());
                // Prepare the HTTP headers
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(contentType));
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + metaData.getFileName());

                // Return the file as a ResponseEntity
                return ResponseEntity.ok()
                        .headers(headers)
                        .body(resource);
            } else {
                // File not found
                return ResponseEntity.notFound().build();
            }


        }
    }

    private String determineContentType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        switch (extension) {
            case "jpg":
            case "jpeg":
                return MediaType.IMAGE_JPEG_VALUE;
            case "png":
                return MediaType.IMAGE_PNG_VALUE;
            case "gif":
                return MediaType.IMAGE_GIF_VALUE;
            // Add more cases for other file types as needed
            default:
                return MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
    }
}
