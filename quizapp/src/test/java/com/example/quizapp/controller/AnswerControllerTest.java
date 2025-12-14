package com.example.quizapp.controller;

import com.example.quizapp.dto.AnswerDTO;
import com.example.quizapp.repository.AnswerRepository;
import com.example.quizapp.repository.QuestionRepository;
import com.example.quizapp.security.CustomUserDetailsService;
import com.example.quizapp.entity.Answer;
import com.example.quizapp.entity.Question;
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
 * Integration tests for AnswerController.
 */
@WebMvcTest(AnswerController.class)
@WithMockUser
class AnswerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AnswerRepository answerRepository;

    @MockitoBean
    private QuestionRepository questionRepository;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    private Answer testAnswer;
    private Question testQuestion;

    @BeforeEach
    void setUp() {
        testQuestion = new Question();
        testQuestion.setId(1L);

        testAnswer = new Answer();
        testAnswer.setId(1L);
        testAnswer.setText("This is an answer");
        testAnswer.setIsCorrect(true);
        testAnswer.setOrderIndex(1);
        testAnswer.setQuestion(testQuestion);
    }

    @Test
    @DisplayName("Should get answers by question id")
    void getAnswersByQuestionId_ShouldReturnAnswers() throws Exception {
        // Controller uses findByQuestionIdOrderByOrderIndexAsc
        when(answerRepository.findByQuestionIdOrderByOrderIndexAsc(1L)).thenReturn(Arrays.asList(testAnswer));

        mockMvc.perform(get("/api/v1/answers/question/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text").value("This is an answer"));
    }

    @Test
    @DisplayName("Should get answer by id")
    void getAnswerById_ShouldReturnAnswer() throws Exception {
        when(answerRepository.findById(1L)).thenReturn(Optional.of(testAnswer));

        mockMvc.perform(get("/api/v1/answers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("This is an answer"));
    }

    @Test
    @DisplayName("Should return 404 when answer not found")
    void getAnswerById_ShouldReturn404_WhenNotFound() throws Exception {
        when(answerRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/answers/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should create answer")
    @WithMockUser(roles = "ADMIN")
    void createAnswer_ShouldReturnCreated() throws Exception {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(testQuestion));
        when(answerRepository.save(any(Answer.class))).thenReturn(testAnswer);

        AnswerDTO dto = new AnswerDTO();
        dto.setText("New answer");
        dto.setIsCorrect(true);
        dto.setQuestionId(1L);

        mockMvc.perform(post("/api/v1/answers")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }
}
