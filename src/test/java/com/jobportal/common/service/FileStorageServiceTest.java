package com.jobportal.common.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import com.jobportal.common.exception.ResourceNotFoundException;

class FileStorageServiceTest {

    private FileStorageService fileStorageService;
    private Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("test-uploads");
        fileStorageService = new FileStorageService(tempDir.toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up temp directory
        Files.walk(tempDir)
                .sorted(java.util.Comparator.reverseOrder())
                .forEach(path -> {
                    try { Files.deleteIfExists(path); } catch (IOException ignored) {}
                });
    }

    @Test
    void storeFile_ValidPdf_ReturnsPath() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "resume.pdf", "application/pdf", "PDF content".getBytes());

        String result = fileStorageService.storeFile(file, "resumes");

        assertThat(result).startsWith("resumes/");
        assertThat(result).endsWith(".pdf");
        assertThat(Files.exists(tempDir.resolve(result))).isTrue();
    }

    @Test
    void storeFile_ValidDocx_ReturnsPath() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "resume.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "DOCX content".getBytes());

        String result = fileStorageService.storeFile(file, "resumes");

        assertThat(result).startsWith("resumes/");
        assertThat(result).endsWith(".docx");
    }

    @Test
    void storeFile_ValidDoc_ReturnsPath() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "resume.doc", "application/msword", "DOC content".getBytes());

        String result = fileStorageService.storeFile(file, "resumes");

        assertThat(result).startsWith("resumes/");
        assertThat(result).endsWith(".doc");
    }

    @Test
    void storeFile_InvalidContentType_ThrowsException() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "image.png", "image/png", "PNG content".getBytes());

        assertThatThrownBy(() -> fileStorageService.storeFile(file, "resumes"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Only PDF, DOC, and DOCX files are allowed");
    }

    @Test
    void storeFile_ExceedsSizeLimit_ThrowsException() {
        byte[] largeContent = new byte[6 * 1024 * 1024]; // 6MB
        MockMultipartFile file = new MockMultipartFile(
                "file", "large.pdf", "application/pdf", largeContent);

        assertThatThrownBy(() -> fileStorageService.storeFile(file, "resumes"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("File size must not exceed 5MB");
    }

    @Test
    void storeFile_EmptyFilename_ThrowsException() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "", "application/pdf", "PDF content".getBytes());

        assertThatThrownBy(() -> fileStorageService.storeFile(file, "resumes"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("File name cannot be empty");
    }

    @Test
    void loadFile_ExistingFile_ReturnsResource() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "resume.pdf", "application/pdf", "PDF content".getBytes());
        String storedPath = fileStorageService.storeFile(file, "resumes");

        Resource resource = fileStorageService.loadFile(storedPath);

        assertThat(resource.exists()).isTrue();
        assertThat(resource.isReadable()).isTrue();
    }

    @Test
    void loadFile_NonExistingFile_ThrowsException() {
        assertThatThrownBy(() -> fileStorageService.loadFile("resumes/nonexistent.pdf"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("File not found");
    }

    @Test
    void deleteFile_ExistingFile_DeletesSuccessfully() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "resume.pdf", "application/pdf", "PDF content".getBytes());
        String storedPath = fileStorageService.storeFile(file, "resumes");

        fileStorageService.deleteFile(storedPath);

        assertThat(Files.exists(tempDir.resolve(storedPath))).isFalse();
    }

    @Test
    void deleteFile_NonExistingFile_DoesNotThrow() {
        // Should not throw exception for non-existing files
        fileStorageService.deleteFile("resumes/nonexistent.pdf");
    }
}
