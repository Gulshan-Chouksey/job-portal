package com.jobportal.common.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jobportal.common.exception.ResourceNotFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${file.upload-dir:uploads}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
            log.info("File storage initialized at: {}", this.fileStorageLocation);
        } catch (IOException ex) {
            log.error("Could not create upload directory: {}", this.fileStorageLocation, ex);
            throw new RuntimeException("Could not create upload directory", ex);
        }
    }

    public String storeFile(MultipartFile file, String subDirectory) {
        log.info("Storing file: {} in sub-directory: {}", file.getOriginalFilename(), subDirectory);

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("File name cannot be empty");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !isAllowedContentType(contentType)) {
            log.warn("Invalid file type: {}", contentType);
            throw new IllegalArgumentException("Only PDF, DOC, and DOCX files are allowed");
        }

        // Validate file size (max 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            log.warn("File size exceeds limit: {} bytes", file.getSize());
            throw new IllegalArgumentException("File size must not exceed 5MB");
        }

        // Generate unique filename
        String fileExtension = getFileExtension(originalFilename);
        String storedFilename = UUID.randomUUID().toString() + fileExtension;

        try {
            Path targetDirectory = this.fileStorageLocation.resolve(subDirectory).normalize();
            Files.createDirectories(targetDirectory);

            Path targetLocation = targetDirectory.resolve(storedFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            String relativePath = subDirectory + "/" + storedFilename;
            log.info("File stored successfully: {}", relativePath);
            return relativePath;

        } catch (IOException ex) {
            log.error("Could not store file: {}", originalFilename, ex);
            throw new RuntimeException("Could not store file " + originalFilename, ex);
        }
    }

    public Resource loadFile(String filePath) {
        log.info("Loading file: {}", filePath);
        try {
            Path file = this.fileStorageLocation.resolve(filePath).normalize();
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                log.warn("File not found: {}", filePath);
                throw new ResourceNotFoundException("File not found: " + filePath);
            }
        } catch (MalformedURLException ex) {
            log.error("Malformed URL for file: {}", filePath, ex);
            throw new ResourceNotFoundException("File not found: " + filePath);
        }
    }

    public void deleteFile(String filePath) {
        log.info("Deleting file: {}", filePath);
        try {
            Path file = this.fileStorageLocation.resolve(filePath).normalize();
            Files.deleteIfExists(file);
            log.info("File deleted successfully: {}", filePath);
        } catch (IOException ex) {
            log.error("Could not delete file: {}", filePath, ex);
        }
    }

    private boolean isAllowedContentType(String contentType) {
        return contentType.equals("application/pdf")
                || contentType.equals("application/msword")
                || contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex >= 0 ? filename.substring(dotIndex) : "";
    }
}
