package com.example.quizapp.controller;

import com.example.quizapp.dto.QuizResultDTO;
import com.example.quizapp.entity.Quiz;
import com.example.quizapp.entity.QuizResult;
import com.example.quizapp.repository.QuizRepository;
import com.example.quizapp.repository.QuizResultRepository;
import com.example.quizapp.security.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for QuizResultController.
 */
@WebMvcTest(QuizResultController.class)
@WithMockUser
class QuizResultControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private QuizResultRepository quizResultRepository;

    @MockitoBean
    private QuizRepository quizRepository;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    private QuizResult testResult;
    private Quiz testQuiz;

    @BeforeEach
    void setUp() {
        testQuiz = new Quiz();
        testQuiz.setId(1L);
        testQuiz.setTitle("Test Quiz");

        testResult = new QuizResult();
        testResult.setId(1L);
        testResult.setNickname("Player1");
        testResult.setScore(90);
        testResult.setMaxScore(100);
        testResult.setQuiz(testQuiz);
    }

    @Test
    @DisplayName("Should get ranking for quiz (top 10)")
    void getRanking_ShouldReturnResults() throws Exception {
        // Controller uses findTop10ByQuizIdOrderByScoreDesc
        when(quizResultRepository.findTop10ByQuizIdOrderByScoreDesc(1L))
                .thenReturn(Arrays.asList(testResult));

        mockMvc.perform(get("/api/v1/results/quiz/1/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nickname").value("Player1"));
    }

    @Test
    @DisplayName("Should get result by id")
    void getResultById_ShouldReturnResult() throws Exception {
        when(quizResultRepository.findById(1L)).thenReturn(Optional.of(testResult));

        mockMvc.perform(get("/api/v1/results/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("Player1"))
                .andExpect(jsonPath("$.score").value(90));
    }

    @Test
    @DisplayName("Should return 404 when result not found")
    void getResultById_ShouldReturn404_WhenNotFound() throws Exception {
        when(quizResultRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/results/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should submit result")
    void submitResult_ShouldReturnCreated() throws Exception {
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(quizResultRepository.save(any(QuizResult.class))).thenReturn(testResult);

        QuizResultDTO dto = new QuizResultDTO();
        dto.setNickname("NewPlayer");
        dto.setScore(85);
        dto.setMaxScore(100);
        dto.setQuizId(1L);

        mockMvc.perform(post("/api/v1/results")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }
}
