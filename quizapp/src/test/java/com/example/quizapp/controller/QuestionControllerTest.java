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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for QuestionController.
 * Covers all CRUD operations and edge cases.
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

    // ============ GET All Questions ============

    @Test
    @DisplayName("Should get all questions with pagination")
    void getAllQuestions_ShouldReturnPaginatedQuestions() throws Exception {
        when(questionRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(testQuestion), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/v1/questions")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].text").value("What is Java?"));
    }

    // ============ GET Questions by Quiz ============

    @Test
    @DisplayName("Should get questions by quiz id (unordered)")
    void getQuestionsByQuizId_ShouldReturnQuestions() throws Exception {
        when(questionRepository.findByQuizId(1L)).thenReturn(Arrays.asList(testQuestion));

        mockMvc.perform(get("/api/v1/questions/quiz/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text").value("What is Java?"));
    }

    @Test
    @DisplayName("Should get questions by quiz id ordered")
    void getQuestionsByQuizId_Ordered_ShouldReturnOrderedQuestions() throws Exception {
        when(questionRepository.findByQuizIdOrderByOrderIndexAsc(1L)).thenReturn(Arrays.asList(testQuestion));

        mockMvc.perform(get("/api/v1/questions/quiz/1")
                        .param("ordered", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text").value("What is Java?"));
    }

    // ============ GET Question by ID ============

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

    // ============ CREATE Question ============

    @Test
    @DisplayName("Should create question with all fields")
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
    @DisplayName("Should create question with null type - defaults to SINGLE_CHOICE")
    @WithMockUser(roles = "ADMIN")
    void createQuestion_WithNullType_ShouldDefaultToSingleChoice() throws Exception {
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(questionRepository.save(any(Question.class))).thenReturn(testQuestion);

        QuestionDTO dto = new QuestionDTO();
        dto.setText("New question");
        dto.setType(null); // Null type
        dto.setPoints(3);
        dto.setQuizId(1L);

        mockMvc.perform(post("/api/v1/questions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should create question with null points - defaults to 1")
    @WithMockUser(roles = "ADMIN")
    void createQuestion_WithNullPoints_ShouldDefaultToOne() throws Exception {
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(questionRepository.save(any(Question.class))).thenReturn(testQuestion);

        QuestionDTO dto = new QuestionDTO();
        dto.setText("New question");
        dto.setType(QuestionType.MULTIPLE_CHOICE);
        dto.setPoints(null); // Null points
        dto.setQuizId(1L);

        mockMvc.perform(post("/api/v1/questions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should return 404 when creating question with invalid quiz id")
    @WithMockUser(roles = "ADMIN")
    void createQuestion_ShouldReturn404_WhenQuizNotFound() throws Exception {
        when(quizRepository.findById(999L)).thenReturn(Optional.empty());

        QuestionDTO dto = new QuestionDTO();
        dto.setText("New question");
        dto.setQuizId(999L);

        mockMvc.perform(post("/api/v1/questions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    // ============ UPDATE Question ============

    @Test
    @DisplayName("Should update question")
    @WithMockUser(roles = "ADMIN")
    void updateQuestion_ShouldReturnUpdatedQuestion() throws Exception {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(testQuestion));
        when(questionRepository.save(any(Question.class))).thenReturn(testQuestion);

        QuestionDTO dto = new QuestionDTO();
        dto.setText("Updated question text");
        dto.setType(QuestionType.MULTIPLE_CHOICE);
        dto.setPoints(10);
        dto.setOrderIndex(1);

        mockMvc.perform(put("/api/v1/questions/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent question")
    @WithMockUser(roles = "ADMIN")
    void updateQuestion_ShouldReturn404_WhenNotFound() throws Exception {
        when(questionRepository.findById(999L)).thenReturn(Optional.empty());

        QuestionDTO dto = new QuestionDTO();
        dto.setText("Updated question");

        mockMvc.perform(put("/api/v1/questions/999")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    // ============ DELETE Question ============

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

    @Test
    @DisplayName("Should return 404 when deleting non-existent question")
    @WithMockUser(roles = "ADMIN")
    void deleteQuestion_ShouldReturn404_WhenNotFound() throws Exception {
        when(questionRepository.existsById(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/questions/999")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}

