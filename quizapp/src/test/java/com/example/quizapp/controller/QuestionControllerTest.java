package com.example.quizapp.controller;

import com.example.quizapp.entity.Question;
import com.example.quizapp.entity.QuestionType;
import com.example.quizapp.entity.Quiz;
import com.example.quizapp.repository.QuestionRepository;
import com.example.quizapp.repository.QuizRepository;
import com.example.quizapp.security.CustomUserDetailsService;
import com.example.quizapp.dto.QuestionDTO;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for QuestionController.
 */
@WebMvcTest(QuestionController.class)
@WithMockUser
class QuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private QuestionRepository questionRepository;

    @MockitoBean
    private QuizRepository quizRepository;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    private Question testQuestion;
    private Quiz testQuiz;

    @BeforeEach
    void setUp() {
        testQuiz = new Quiz();
        testQuiz.setId(1L);
        testQuiz.setTitle("Test Quiz");

        testQuestion = new Question();
        testQuestion.setId(1L);
        testQuestion.setText("What is Java?");
        testQuestion.setType(QuestionType.SINGLE_CHOICE);
        testQuestion.setPoints(5);
        testQuestion.setQuiz(testQuiz);
    }

    @Test
    @DisplayName("Should get questions by quiz id")
    void getQuestionsByQuizId_ShouldReturnQuestions() throws Exception {
        when(questionRepository.findByQuizId(1L)).thenReturn(Arrays.asList(testQuestion));

        mockMvc.perform(get("/api/v1/questions/quiz/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text").value("What is Java?"));
    }

    @Test
    @DisplayName("Should get question by id")
    void getQuestionById_ShouldReturnQuestion() throws Exception {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(testQuestion));

        mockMvc.perform(get("/api/v1/questions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("What is Java?"));
    }

    @Test
    @DisplayName("Should return 404 when question not found")
    void getQuestionById_ShouldReturn404_WhenNotFound() throws Exception {
        when(questionRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/questions/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should create question")
    @WithMockUser(roles = "ADMIN")
    void createQuestion_ShouldReturnCreated() throws Exception {
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(questionRepository.save(any(Question.class))).thenReturn(testQuestion);

        QuestionDTO dto = new QuestionDTO();
        dto.setText("New question");
        dto.setType(QuestionType.TRUE_FALSE);
        dto.setPoints(3);
        dto.setQuizId(1L);

        mockMvc.perform(post("/api/v1/questions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should delete question")
    @WithMockUser(roles = "ADMIN")
    void deleteQuestion_ShouldReturnNoContent() throws Exception {
        when(questionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(questionRepository).deleteById(1L);

        mockMvc.perform(delete("/api/v1/questions/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
