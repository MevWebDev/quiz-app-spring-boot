package com.example.quizapp.controller;

import com.example.quizapp.dto.QuizDTO;
import com.example.quizapp.security.CustomUserDetailsService;
import com.example.quizapp.service.FileService;
import com.example.quizapp.service.QuizService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for FileController - file upload, download, and export.
 */
@WebMvcTest(FileController.class)
@WithMockUser(roles = "ADMIN")
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FileService fileService;

    @MockitoBean
    private QuizService quizService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    private QuizDTO testQuizDTO;

    @BeforeEach
    void setUp() {
        testQuizDTO = new QuizDTO();
        testQuizDTO.setId(1L);
        testQuizDTO.setTitle("Test Quiz");
        testQuizDTO.setDescription("Test Description");
        testQuizDTO.setTimeLimit(300);
        testQuizDTO.setQuestionCount(5);
    }

    @Test
    @DisplayName("Should upload file successfully")
    void uploadFile_ShouldRedirectWithSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "test content".getBytes());
        
        when(fileService.uploadFile(any())).thenReturn("test.txt");

        mockMvc.perform(multipart("/files/upload")
                        .file(file)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/quizzes"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @DisplayName("Should handle upload error")
    void uploadFile_ShouldRedirectWithError() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "test content".getBytes());
        
        when(fileService.uploadFile(any())).thenThrow(new java.io.IOException("Upload failed"));

        mockMvc.perform(multipart("/files/upload")
                        .file(file)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/quizzes"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    @DisplayName("Should download file")
    void downloadFile_ShouldReturnFile() throws Exception {
        Resource resource = new ByteArrayResource("file content".getBytes());
        when(fileService.downloadFile("test.txt")).thenReturn(resource);

        mockMvc.perform(get("/files/download/test.txt"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"test.txt\""));
    }

    @Test
    @DisplayName("Should export quizzes to CSV")
    void exportQuizzesToCsv_ShouldReturnCsvFile() throws Exception {
        when(quizService.getAllQuizzes(any()))
                .thenReturn(new PageImpl<>(Arrays.asList(testQuizDTO), PageRequest.of(0, 1000), 1));
        when(fileService.exportToCsv(any(), any())).thenReturn("ID,Title\n1,Test Quiz".getBytes());

        mockMvc.perform(get("/files/export/quizzes/csv"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/csv"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"quizzes.csv\""));
    }
}
