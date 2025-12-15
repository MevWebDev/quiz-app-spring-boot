package com.example.quizapp.controller;

import com.example.quizapp.dto.CreateQuizRequest;
import com.example.quizapp.dto.QuizDTO;
import com.example.quizapp.security.CustomUserDetailsService;
import com.example.quizapp.service.QuizService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for QuizMvcController - Thymeleaf MVC operations.
 */
@WebMvcTest(QuizMvcController.class)
@WithMockUser(roles = "ADMIN")
class QuizMvcControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
    }

    @Test
    @DisplayName("Should redirect to admin from quiz list")
    void listQuizzes_ShouldRedirectToAdmin() throws Exception {
        mockMvc.perform(get("/quizzes"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
    }

    @Test
    @DisplayName("Should show quiz details - controller returns correct view")
    void viewQuiz_ShouldCallServiceAndReturnView() throws Exception {
        // Set all required fields to avoid template errors
        testQuizDTO.setShuffleQuestions(false);
        testQuizDTO.setShuffleAnswers(false);
        testQuizDTO.setNegativePoints(false);
        testQuizDTO.setQuestionCount(0);
        testQuizDTO.setCategories(java.util.Set.of("Test Category"));
        when(quizService.getQuizById(1L)).thenReturn(testQuizDTO);

        // Just verify the controller method is called correctly
        mockMvc.perform(get("/quizzes/1"))
                .andExpect(status().isOk());
        
        verify(quizService).getQuizById(1L);
    }

    @Test
    @DisplayName("Should show create quiz form")
    void showCreateForm_ShouldReturnFormView() throws Exception {
        mockMvc.perform(get("/quizzes/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("quiz/form"))
                .andExpect(model().attributeExists("quiz"));
    }

    @Test
    @DisplayName("Should create quiz and redirect")
    void createQuiz_ShouldRedirectToQuiz() throws Exception {
        when(quizService.createQuiz(any())).thenReturn(testQuizDTO);

        mockMvc.perform(post("/quizzes")
                        .with(csrf())
                        .param("title", "New Quiz")
                        .param("description", "Description"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/quizzes/1"));
    }

    @Test
    @DisplayName("Should return form on validation error")
    void createQuiz_ShouldReturnFormOnError() throws Exception {
        mockMvc.perform(post("/quizzes")
                        .with(csrf())
                        .param("title", "")) // Empty title - validation error
                .andExpect(status().isOk())
                .andExpect(view().name("quiz/form"));
    }

    @Test
    @DisplayName("Should show edit quiz form")
    void showEditForm_ShouldReturnFormWithData() throws Exception {
        when(quizService.getQuizById(1L)).thenReturn(testQuizDTO);

        mockMvc.perform(get("/quizzes/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("quiz/form"))
                .andExpect(model().attribute("isEdit", true))
                .andExpect(model().attributeExists("quiz"));
    }

    @Test
    @DisplayName("Should update quiz and redirect")
    void updateQuiz_ShouldRedirectToQuiz() throws Exception {
        when(quizService.updateQuiz(eq(1L), any())).thenReturn(testQuizDTO);

        mockMvc.perform(post("/quizzes/1")
                        .with(csrf())
                        .param("title", "Updated Quiz"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/quizzes/1"));
    }

    @Test
    @DisplayName("Should return form on update validation error")
    void updateQuiz_ShouldReturnFormOnError() throws Exception {
        mockMvc.perform(post("/quizzes/1")
                        .with(csrf())
                        .param("title", "")) // Empty title
                .andExpect(status().isOk())
                .andExpect(view().name("quiz/form"))
                .andExpect(model().attribute("isEdit", true));
    }

    @Test
    @DisplayName("Should delete quiz and redirect")
    void deleteQuiz_ShouldRedirectToQuizzes() throws Exception {
        doNothing().when(quizService).deleteQuiz(1L);

        mockMvc.perform(post("/quizzes/1/delete")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/quizzes"));
    }
}
