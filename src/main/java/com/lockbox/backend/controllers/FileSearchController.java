package com.lockbox.backend.controllers;

import com.lockbox.backend.models.MetaData;
import com.lockbox.backend.repositories.MetaDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller responsible for handling file search and retrieval operations.
 */
@Controller
@CrossOrigin
public class FileSearchController {
    private final MetaDataRepository metaDataRepository;

    @Autowired
    public FileSearchController(MetaDataRepository metaDataRepository) {
        this.metaDataRepository = metaDataRepository;
    }

    /**
     * Retrieves and returns all public (non-private and non-encrypted) files stored in the system.
     *
     * @return A ResponseEntity containing a list of public file metadata in JSON format.
     */
    @GetMapping("/public-files")
    public ResponseEntity<List<Map<String, Object>>> getAllPublicFiles() {
        List<MetaData> allFiles = (List<MetaData>)metaDataRepository.findAll();

        List<Map<String, Object>> publicFiles = allFiles.stream()
                .filter(file -> !file.isPrivate() && !file.isEncrypted())
                .map(this::mapMetaDataToJson)
                .collect(Collectors.toList());

        return ResponseEntity.ok(publicFiles);
    }

    /**
     * Retrieves and returns all files that match the specified name.
     *
     * @param name The name of the file to search for.
     * @return A ResponseEntity containing a list of file metadata that matches the search criteria.
     */
    @GetMapping("/search-files")
    public ResponseEntity<List<Map<String, Object>>> searchFilesByName(@RequestParam String name) {
        List<MetaData> matchingFiles = metaDataRepository.findByFileNameContaining(name);

        List<Map<String, Object>> filesJson = matchingFiles.stream()
                .filter(file -> !file.isPrivate() && !file.isEncrypted())
                .map(this::mapMetaDataToJson)
                .collect(Collectors.toList());

        return ResponseEntity.ok(filesJson);
    }

    /**
     * Maps MetaData object to a JSON-friendly representation.
     *
     * @param metaData The MetaData object to be transformed.
     * @return A map representing the essential information of the file.
     */
    private Map<String, Object> mapMetaDataToJson(MetaData metaData) {
        return Map.of(
                "fileName", metaData.getFileName(),
                "accessLink", metaData.getLink(),
                "uploadDate", metaData.getUploadDate(),
                "size", metaData.getSize()
        );
    }
}