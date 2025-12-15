package com.example.quizapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FileService - file upload, download, and export operations.
 */
class FileServiceTest {

    @TempDir
    Path tempDir;

    private FileService fileService;

    @BeforeEach
    void setUp() {
        fileService = new FileService(tempDir.toString());
    }

    // ============ Upload File Tests ============

    @Test
    @DisplayName("Should upload file successfully")
    void uploadFile_ShouldReturnNewFilename() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "Hello World".getBytes());

        String result = fileService.uploadFile(file);

        assertNotNull(result);
        assertTrue(result.endsWith(".txt"));
        assertTrue(Files.exists(tempDir.resolve(result)));
    }

    @Test
    @DisplayName("Should upload file without extension")
    void uploadFile_WithoutExtension_ShouldWork() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "testfile", "application/octet-stream", "data".getBytes());

        String result = fileService.uploadFile(file);

        assertNotNull(result);
        assertFalse(result.contains(".")); // No extension added
    }

    @Test
    @DisplayName("Should upload file with null original filename")
    void uploadFile_WithNullFilename_ShouldWork() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", null, "text/plain", "content".getBytes());

        String result = fileService.uploadFile(file);

        assertNotNull(result);
    }

    // ============ Download File Tests ============

    @Test
    @DisplayName("Should download existing file")
    void downloadFile_WhenFileExists_ShouldReturnResource() throws IOException {
        // Create a file in temp directory
        Path testFile = tempDir.resolve("existing.txt");
        Files.write(testFile, "Test content".getBytes());

        Resource resource = fileService.downloadFile("existing.txt");

        assertNotNull(resource);
        assertTrue(resource.exists());
        assertTrue(resource.isReadable());
    }

    @Test
    @DisplayName("Should throw exception when file not found")
    void downloadFile_WhenFileNotExists_ShouldThrowException() {
        assertThrows(RuntimeException.class, () -> 
            fileService.downloadFile("nonexistent.txt"));
    }

    // ============ Export to CSV Tests ============

    @Test
    @DisplayName("Should export data to CSV format")
    void exportToCsv_ShouldReturnCsvBytes() {
        String[] headers = {"ID", "Name", "Age"};
        List<String[]> data = Arrays.asList(
                new String[]{"1", "Alice", "25"},
                new String[]{"2", "Bob", "30"}
        );

        byte[] result = fileService.exportToCsv(data, headers);

        String csvContent = new String(result);
        assertTrue(csvContent.contains("ID,Name,Age"));
        assertTrue(csvContent.contains("1,Alice,25"));
        assertTrue(csvContent.contains("2,Bob,30"));
    }

    @Test
    @DisplayName("Should export empty data to CSV")
    void exportToCsv_WithEmptyData_ShouldReturnOnlyHeaders() {
        String[] headers = {"Column1", "Column2"};
        List<String[]> data = List.of();

        byte[] result = fileService.exportToCsv(data, headers);

        String csvContent = new String(result);
        assertTrue(csvContent.contains("Column1,Column2"));
        assertEquals(1, csvContent.split("\n").length); // Only headers
    }

    // ============ Delete File Tests ============

    @Test
    @DisplayName("Should delete existing file")
    void deleteFile_WhenFileExists_ShouldReturnTrue() throws IOException {
        // Create a file to delete
        Path testFile = tempDir.resolve("todelete.txt");
        Files.write(testFile, "Delete me".getBytes());

        boolean result = fileService.deleteFile("todelete.txt");

        assertTrue(result);
        assertFalse(Files.exists(testFile));
    }

    @Test
    @DisplayName("Should return false when deleting non-existent file")
    void deleteFile_WhenFileNotExists_ShouldReturnFalse() {
        boolean result = fileService.deleteFile("nonexistent.txt");

        assertFalse(result);
    }
}
