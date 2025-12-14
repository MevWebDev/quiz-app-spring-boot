package com.example.quizapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

/**
 * Service for file upload, download, and export operations.
 */
@Service
public class FileService {

    private final Path uploadDir;

    public FileService(@Value("${app.upload.dir:uploads}") String uploadDirPath) {
        this.uploadDir = Paths.get(uploadDirPath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    /**
     * Upload a file using MultipartFile and Files.copy
     */
    public String uploadFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String newFilename = UUID.randomUUID().toString() + extension;
        Path targetLocation = uploadDir.resolve(newFilename);
        
        // Using Files.copy as per requirements
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        
        return newFilename;
    }

    /**
     * Download a file as Resource
     */
    public Resource downloadFile(String filename) {
        try {
            Path filePath = uploadDir.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File not found: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("File not found: " + filename, e);
        }
    }

    /**
     * Export data to CSV format
     */
    public byte[] exportToCsv(List<String[]> data, String[] headers) {
        StringBuilder sb = new StringBuilder();
        
        // Add headers
        sb.append(String.join(",", headers)).append("\n");
        
        // Add data rows
        for (String[] row : data) {
            sb.append(String.join(",", row)).append("\n");
        }
        
        return sb.toString().getBytes();
    }

    /**
     * Delete a file
     */
    public boolean deleteFile(String filename) {
        try {
            Path filePath = uploadDir.resolve(filename).normalize();
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            return false;
        }
    }
}
