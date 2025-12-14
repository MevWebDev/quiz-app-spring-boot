package com.example.quizapp.controller;

import com.example.quizapp.dto.CreateQuizRequest;
import com.example.quizapp.dto.QuizDTO;
import com.example.quizapp.exception.ResourceNotFoundException;
import com.example.quizapp.security.CustomUserDetailsService;
import com.example.quizapp.service.QuizService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for QuizController.
 * Uses @WebMvcTest, MockMvc, perform(), andExpect(), @WithMockUser.
 * Contains 5+ business scenarios.
 */
@WebMvcTest(QuizController.class)
@WithMockUser  // Default authenticated user for all tests
class QuizControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private QuizService quizService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    private QuizDTO testQuizDTO;
    private CreateQuizRequest createRequest;

    @BeforeEach
    void setUp() {
        testQuizDTO = new QuizDTO();
        testQuizDTO.setId(1L);
        testQuizDTO.setTitle("Test Quiz");
        testQuizDTO.setDescription("Test Description");

        createRequest = new CreateQuizRequest();
        createRequest.setTitle("New Quiz");
        createRequest.setDescription("New Description");
    }

    // ============ Happy Path Tests ============

    @Test
    @DisplayName("Scenario 1: Get all quizzes - authenticated user")
    void getAllQuizzes_ShouldReturnQuizzes() throws Exception {
        // Given
        when(quizService.getAllQuizzes(any()))
                .thenReturn(new PageImpl<>(Arrays.asList(testQuizDTO), PageRequest.of(0, 10), 1));

        // When/Then - perform() and andExpect()
        mockMvc.perform(get("/api/v1/quizzes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Test Quiz"))
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    @DisplayName("Scenario 2: Get quiz by ID - returns quiz")
    void getQuizById_ShouldReturnQuiz_WhenExists() throws Exception {
        // Given
        when(quizService.getQuizById(1L)).thenReturn(testQuizDTO);

        // When/Then
        mockMvc.perform(get("/api/v1/quizzes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Quiz"));
    }

    @Test
    @DisplayName("Scenario 3: Create quiz with valid data - admin user")
    @WithMockUser(roles = "ADMIN")
    void createQuiz_ShouldReturnCreated_WhenValid() throws Exception {
        // Given
        testQuizDTO.setId(2L);
        testQuizDTO.setTitle("New Quiz");
        when(quizService.createQuiz(any(CreateQuizRequest.class))).thenReturn(testQuizDTO);

        // When/Then
        mockMvc.perform(post("/api/v1/quizzes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Quiz"));
    }

    @Test
    @DisplayName("Scenario 4: Update quiz successfully - admin user")
    @WithMockUser(roles = "ADMIN")
    void updateQuiz_ShouldReturnUpdatedQuiz() throws Exception {
        // Given
        testQuizDTO.setTitle("Updated Quiz");
        when(quizService.updateQuiz(eq(1L), any(CreateQuizRequest.class))).thenReturn(testQuizDTO);

        // When/Then
        mockMvc.perform(put("/api/v1/quizzes/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Scenario 5: Delete quiz successfully - admin user")
    @WithMockUser(roles = "ADMIN")
    void deleteQuiz_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(quizService).deleteQuiz(1L);

        // When/Then
        mockMvc.perform(delete("/api/v1/quizzes/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    // ============ Error Cases ============

    @Test
    @DisplayName("Error Case: Quiz not found - returns 404")
    void getQuizById_ShouldReturn404_WhenNotFound() throws Exception {
        // Given
        when(quizService.getQuizById(999L))
                .thenThrow(new ResourceNotFoundException("Quiz", 999L));

        // When/Then
        mockMvc.perform(get("/api/v1/quizzes/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Error Case: Create quiz with invalid data - returns 400")
    @WithMockUser(roles = "ADMIN")
    void createQuiz_ShouldReturn400_WhenInvalid() throws Exception {
        // Given - empty title (validation error)
        CreateQuizRequest invalidRequest = new CreateQuizRequest();
        invalidRequest.setTitle("");  // Invalid - blank

        // When/Then
        mockMvc.perform(post("/api/v1/quizzes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Search quizzes by title")
    void searchQuizzes_ShouldReturnMatchingQuizzes() throws Exception {
        // Given
        when(quizService.searchQuizzes("test")).thenReturn(Arrays.asList(testQuizDTO));

        // When/Then
        mockMvc.perform(get("/api/v1/quizzes/search")
                        .param("title", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Quiz"));
    }
}
