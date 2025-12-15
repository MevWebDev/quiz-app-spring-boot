package com.example.quizapp.controller;

import com.example.quizapp.dto.QuestionDTO;
import com.example.quizapp.dto.QuizDTO;
import com.example.quizapp.entity.*;
import com.example.quizapp.repository.*;
import com.example.quizapp.security.CustomUserDetailsService;
import com.example.quizapp.service.QuestionService;
import com.example.quizapp.service.QuizService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for AdminController - admin panel CRUD operations.
 */
@WebMvcTest(AdminController.class)
@WithMockUser(roles = "ADMIN")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QuizRepository quizRepository;

    @MockitoBean
    private QuestionRepository questionRepository;

    @MockitoBean
    private AnswerRepository answerRepository;

    @MockitoBean
    private QuizService quizService;

    @MockitoBean
    private QuestionService questionService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    private Quiz testQuiz;
    private QuizDTO testQuizDTO;
    private Question testQuestion;
    private QuestionDTO testQuestionDTO;
    private Answer testAnswer;

    @BeforeEach
    void setUp() {
        testQuiz = new Quiz();
        testQuiz.setId(1L);
        testQuiz.setTitle("Test Quiz");
        testQuiz.setDescription("Test Description");

        testQuizDTO = new QuizDTO();
        testQuizDTO.setId(1L);
        testQuizDTO.setTitle("Test Quiz");
        testQuizDTO.setDescription("Test Description");

        testQuestion = new Question();
        testQuestion.setId(1L);
        testQuestion.setText("Test Question");
        testQuestion.setType(QuestionType.SINGLE_CHOICE);
        testQuestion.setPoints(1);
        testQuestion.setQuiz(testQuiz);

        testQuestionDTO = new QuestionDTO();
        testQuestionDTO.setId(1L);
        testQuestionDTO.setText("Test Question");
        testQuestionDTO.setType(QuestionType.SINGLE_CHOICE);
        testQuestionDTO.setPoints(1);
        testQuestionDTO.setQuizId(1L);

        testAnswer = new Answer();
        testAnswer.setId(1L);
        testAnswer.setText("Test Answer");
        testAnswer.setIsCorrect(true);
        testAnswer.setQuestion(testQuestion);
    }

    // ============ Dashboard Tests ============

    @Test
    @DisplayName("Should display admin dashboard")
    void dashboard_ShouldReturnDashboardView() throws Exception {
        when(quizService.getAllQuizzes(any()))
                .thenReturn(new PageImpl<>(Arrays.asList(testQuizDTO), PageRequest.of(0, 100), 1));
        when(quizRepository.count()).thenReturn(1L);
        when(questionRepository.count()).thenReturn(5L);
        when(answerRepository.count()).thenReturn(20L);

        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attributeExists("quizzes"))
                .andExpect(model().attributeExists("totalQuizzes"));
    }

    // ============ Quiz Management Tests ============

    @Test
    @DisplayName("Should show new quiz form")
    void newQuizForm_ShouldReturnFormView() throws Exception {
        mockMvc.perform(get("/admin/quiz/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/quiz-form"))
                .andExpect(model().attributeExists("quiz"))
                .andExpect(model().attribute("isEdit", false));
    }

    @Test
    @DisplayName("Should create quiz and redirect")
    void createQuiz_ShouldRedirectToQuestions() throws Exception {
        when(quizService.createQuiz(any())).thenReturn(testQuizDTO);

        mockMvc.perform(post("/admin/quiz")
                        .with(csrf())
                        .param("title", "New Quiz")
                        .param("description", "New Description"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/quiz/1/questions"));
    }

    @Test
    @DisplayName("Should show edit quiz form")
    void editQuizForm_ShouldReturnFormWithData() throws Exception {
        when(quizService.getQuizById(1L)).thenReturn(testQuizDTO);

        mockMvc.perform(get("/admin/quiz/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/quiz-form"))
                .andExpect(model().attributeExists("quiz"))
                .andExpect(model().attribute("isEdit", true));
    }

    @Test
    @DisplayName("Should update quiz and redirect")
    void updateQuiz_ShouldRedirectToAdmin() throws Exception {
        when(quizService.updateQuiz(eq(1L), any())).thenReturn(testQuizDTO);

        mockMvc.perform(post("/admin/quiz/1")
                        .with(csrf())
                        .param("title", "Updated Quiz"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
    }

    @Test
    @DisplayName("Should delete quiz and redirect")
    void deleteQuiz_ShouldRedirectToAdmin() throws Exception {
        doNothing().when(quizService).deleteQuiz(1L);

        mockMvc.perform(post("/admin/quiz/1/delete")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
    }

    // ============ Question Management Tests ============

    @Test
    @DisplayName("Should show manage questions page")
    void manageQuestions_ShouldReturnQuestionsView() throws Exception {
        when(quizService.getQuizById(1L)).thenReturn(testQuizDTO);
        when(questionService.getQuestionsByQuiz(1L, true))
                .thenReturn(Arrays.asList(testQuestionDTO));

        mockMvc.perform(get("/admin/quiz/1/questions"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/questions"))
                .andExpect(model().attributeExists("quiz"))
                .andExpect(model().attributeExists("questions"));
    }

    @Test
    @DisplayName("Should add question and redirect")
    void addQuestion_ShouldRedirectToQuestions() throws Exception {
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(questionRepository.findByQuizId(1L)).thenReturn(List.of());
        when(questionRepository.save(any())).thenReturn(testQuestion);

        mockMvc.perform(post("/admin/quiz/1/questions")
                        .with(csrf())
                        .param("text", "New Question")
                        .param("type", "SINGLE_CHOICE")
                        .param("points", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/quiz/1/questions"));
    }

    @Test
    @DisplayName("Should delete question and redirect")
    void deleteQuestion_ShouldRedirectToQuestions() throws Exception {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(testQuestion));
        doNothing().when(questionRepository).delete(any());

        mockMvc.perform(post("/admin/questions/1/delete")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/quiz/1/questions"));
    }

    @Test
    @DisplayName("Should update question and redirect")
    void updateQuestion_ShouldRedirectToQuestions() throws Exception {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(testQuestion));
        when(questionRepository.save(any())).thenReturn(testQuestion);

        mockMvc.perform(post("/admin/questions/1/update")
                        .with(csrf())
                        .param("text", "Updated Question")
                        .param("type", "MULTIPLE_CHOICE")
                        .param("points", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/quiz/1/questions"));
    }

    // ============ Answer Management Tests ============

    @Test
    @DisplayName("Should show manage answers page")
    void manageAnswers_ShouldReturnAnswersView() throws Exception {
        when(questionService.getQuestionById(1L)).thenReturn(testQuestionDTO);
        when(answerRepository.findByQuestionIdOrderByOrderIndexAsc(1L))
                .thenReturn(Arrays.asList(testAnswer));

        mockMvc.perform(get("/admin/questions/1/answers"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/answers"))
                .andExpect(model().attributeExists("question"))
                .andExpect(model().attributeExists("answers"));
    }

    @Test
    @DisplayName("Should add answer and redirect")
    void addAnswer_ShouldRedirectToAnswers() throws Exception {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(testQuestion));
        when(answerRepository.findByQuestionId(1L)).thenReturn(List.of());
        when(answerRepository.save(any())).thenReturn(testAnswer);

        mockMvc.perform(post("/admin/questions/1/answers")
                        .with(csrf())
                        .param("text", "New Answer")
                        .param("isCorrect", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/questions/1/answers"));
    }

    @Test
    @DisplayName("Should delete answer and redirect")
    void deleteAnswer_ShouldRedirectToAnswers() throws Exception {
        when(answerRepository.findById(1L)).thenReturn(Optional.of(testAnswer));
        doNothing().when(answerRepository).delete(any());

        mockMvc.perform(post("/admin/answers/1/delete")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/questions/1/answers"));
    }
}
