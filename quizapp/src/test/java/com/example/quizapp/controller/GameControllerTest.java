package com.example.quizapp.controller;

import com.example.quizapp.dto.QuizDTO;
import com.example.quizapp.entity.*;
import com.example.quizapp.repository.*;
import com.example.quizapp.security.CustomUserDetailsService;
import com.example.quizapp.service.QuizService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for GameController - quiz game flow.
 * Covers all question types and branches for high coverage.
 */
@WebMvcTest(GameController.class)
@WithMockUser
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QuizRepository quizRepository;

    @MockitoBean
    private QuestionRepository questionRepository;

    @MockitoBean
    private AnswerRepository answerRepository;

    @MockitoBean
    private QuizResultRepository quizResultRepository;

    @MockitoBean
    private QuizService quizService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    private Quiz testQuiz;
    private QuizDTO testQuizDTO;
    private Question testQuestion;
    private Answer testAnswer;

    @BeforeEach
    void setUp() {
        testQuiz = new Quiz();
        testQuiz.setId(1L);
        testQuiz.setTitle("Test Quiz");
        testQuiz.setDescription("Test Description");
        testQuiz.setTimeLimit(300);
        testQuiz.setShuffleQuestions(false);
        testQuiz.setShuffleAnswers(false);
        testQuiz.setNegativePoints(false);

        testQuizDTO = new QuizDTO();
        testQuizDTO.setId(1L);
        testQuizDTO.setTitle("Test Quiz");
        testQuizDTO.setDescription("Test Description");
        testQuizDTO.setTimeLimit(300);
        testQuizDTO.setShuffleQuestions(false);
        testQuizDTO.setShuffleAnswers(false);
        testQuizDTO.setNegativePoints(false);

        testQuestion = new Question();
        testQuestion.setId(1L);
        testQuestion.setText("Test Question");
        testQuestion.setType(QuestionType.SINGLE_CHOICE);
        testQuestion.setPoints(1);
        testQuestion.setQuiz(testQuiz);

        testAnswer = new Answer();
        testAnswer.setId(1L);
        testAnswer.setText("Correct Answer");
        testAnswer.setIsCorrect(true);
        testAnswer.setOrderIndex(1);
        testAnswer.setQuestion(testQuestion);
    }

    // ============ Home Page Tests ============

    @Test
    @DisplayName("Should display home page with quiz list")
    void home_ShouldReturnHomeView() throws Exception {
        when(quizService.getAllQuizzes(any()))
                .thenReturn(new PageImpl<>(Arrays.asList(testQuizDTO), PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("game/home"))
                .andExpect(model().attributeExists("quizzes"));
    }

    // ============ Start Quiz Tests ============

    @Test
    @DisplayName("Should show start quiz page")
    void startQuiz_ShouldReturnStartView() throws Exception {
        when(quizService.getQuizById(1L)).thenReturn(testQuizDTO);

        mockMvc.perform(get("/play/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("game/start"))
                .andExpect(model().attributeExists("quiz"));
    }

    // ============ Play Quiz Tests ============

    @Test
    @DisplayName("Should show play quiz page with questions")
    void playQuiz_ShouldReturnPlayViewWithQuestions() throws Exception {
        when(quizService.getQuizById(1L)).thenReturn(testQuizDTO);
        when(questionRepository.findByQuizId(1L)).thenReturn(Arrays.asList(testQuestion));
        when(answerRepository.findByQuestionIdOrderByOrderIndexAsc(1L)).thenReturn(Arrays.asList(testAnswer));

        mockMvc.perform(post("/play/1")
                        .with(csrf())
                        .param("nickname", "TestPlayer"))
                .andExpect(status().isOk())
                .andExpect(view().name("game/play"))
                .andExpect(model().attributeExists("quiz"))
                .andExpect(model().attributeExists("questions"))
                .andExpect(model().attribute("nickname", "TestPlayer"));
    }

    @Test
    @DisplayName("Should handle shuffled questions")
    void playQuiz_ShouldShuffleQuestionsWhenEnabled() throws Exception {
        testQuizDTO.setShuffleQuestions(true);
        when(quizService.getQuizById(1L)).thenReturn(testQuizDTO);
        when(questionRepository.findByQuizId(1L)).thenReturn(Arrays.asList(testQuestion));
        when(answerRepository.findByQuestionIdOrderByOrderIndexAsc(1L)).thenReturn(Arrays.asList(testAnswer));

        mockMvc.perform(post("/play/1")
                        .with(csrf())
                        .param("nickname", "Player"))
                .andExpect(status().isOk())
                .andExpect(view().name("game/play"));
    }

    @Test
    @DisplayName("Should handle shuffled answers")
    void playQuiz_ShouldShuffleAnswersWhenEnabled() throws Exception {
        testQuizDTO.setShuffleAnswers(true);
        when(quizService.getQuizById(1L)).thenReturn(testQuizDTO);
        when(questionRepository.findByQuizId(1L)).thenReturn(Arrays.asList(testQuestion));
        when(answerRepository.findByQuestionIdOrderByOrderIndexAsc(1L)).thenReturn(Arrays.asList(testAnswer));

        mockMvc.perform(post("/play/1")
                        .with(csrf())
                        .param("nickname", "Player"))
                .andExpect(status().isOk())
                .andExpect(view().name("game/play"));
    }

    // ============ Submit Quiz Tests - Basic ============

    @Test
    @DisplayName("Should submit quiz and redirect to result")
    void submitQuiz_ShouldRedirectToResult() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("nickname", "TestPlayer");
        session.setAttribute("startTime", System.currentTimeMillis());

        when(quizService.getQuizById(1L)).thenReturn(testQuizDTO);
        when(questionRepository.findByQuizId(1L)).thenReturn(Arrays.asList(testQuestion));
        when(answerRepository.findByQuestionIdAndIsCorrectTrue(1L)).thenReturn(Arrays.asList(testAnswer));
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(quizResultRepository.save(any())).thenReturn(new QuizResult());

        mockMvc.perform(post("/submit/1")
                        .with(csrf())
                        .session(session)
                        .param("answer_1", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/result/1"));
    }

    @Test
    @DisplayName("Should handle anonymous submission")
    void submitQuiz_ShouldHandleAnonymousUser() throws Exception {
        MockHttpSession session = new MockHttpSession();
        // No nickname in session

        when(quizService.getQuizById(1L)).thenReturn(testQuizDTO);
        when(questionRepository.findByQuizId(1L)).thenReturn(Arrays.asList(testQuestion));
        when(answerRepository.findByQuestionIdAndIsCorrectTrue(1L)).thenReturn(Arrays.asList(testAnswer));
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(quizResultRepository.save(any())).thenReturn(new QuizResult());

        mockMvc.perform(post("/submit/1")
                        .with(csrf())
                        .session(session)
                        .param("answer_1", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/result/1"));
    }

    @Test
    @DisplayName("Should apply negative points when enabled")
    void submitQuiz_ShouldApplyNegativePoints() throws Exception {
        testQuizDTO.setNegativePoints(true);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("nickname", "Player");
        session.setAttribute("startTime", System.currentTimeMillis());

        when(quizService.getQuizById(1L)).thenReturn(testQuizDTO);
        when(questionRepository.findByQuizId(1L)).thenReturn(Arrays.asList(testQuestion));
        when(answerRepository.findByQuestionIdAndIsCorrectTrue(1L)).thenReturn(Arrays.asList(testAnswer));
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(quizResultRepository.save(any())).thenReturn(new QuizResult());

        mockMvc.perform(post("/submit/1")
                        .with(csrf())
                        .session(session)
                        .param("answer_1", "99")) // Wrong answer
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Should handle time limit exceeded")
    void submitQuiz_ShouldHandleTimeLimitExceeded() throws Exception {
        testQuizDTO.setTimeLimit(1); // 1 second limit
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("nickname", "Player");
        session.setAttribute("startTime", System.currentTimeMillis() - 5000); // Started 5 seconds ago

        when(quizService.getQuizById(1L)).thenReturn(testQuizDTO);
        when(questionRepository.findByQuizId(1L)).thenReturn(Arrays.asList(testQuestion));
        when(answerRepository.findByQuestionIdAndIsCorrectTrue(1L)).thenReturn(Arrays.asList(testAnswer));
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(quizResultRepository.save(any())).thenReturn(new QuizResult());

        mockMvc.perform(post("/submit/1")
                        .with(csrf())
                        .session(session)
                        .param("answer_1", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    @DisplayName("Should handle no start time in session")
    void submitQuiz_ShouldHandleNoStartTime() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("nickname", "Player");
        // No startTime

        when(quizService.getQuizById(1L)).thenReturn(testQuizDTO);
        when(questionRepository.findByQuizId(1L)).thenReturn(Arrays.asList(testQuestion));
        when(answerRepository.findByQuestionIdAndIsCorrectTrue(1L)).thenReturn(Arrays.asList(testAnswer));
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(quizResultRepository.save(any())).thenReturn(new QuizResult());

        mockMvc.perform(post("/submit/1")
                        .with(csrf())
                        .session(session)
                        .param("answer_1", "1"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Should handle null time limit")
    void submitQuiz_ShouldHandleNullTimeLimit() throws Exception {
        testQuizDTO.setTimeLimit(null);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("nickname", "Player");
        session.setAttribute("startTime", System.currentTimeMillis());

        when(quizService.getQuizById(1L)).thenReturn(testQuizDTO);
        when(questionRepository.findByQuizId(1L)).thenReturn(Arrays.asList(testQuestion));
        when(answerRepository.findByQuestionIdAndIsCorrectTrue(1L)).thenReturn(Arrays.asList(testAnswer));
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(quizResultRepository.save(any())).thenReturn(new QuizResult());

        mockMvc.perform(post("/submit/1")
                        .with(csrf())
                        .session(session)
                        .param("answer_1", "1"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Should handle quiz not found in repository")
    void submitQuiz_ShouldHandleQuizNotFound() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("nickname", "Player");
        session.setAttribute("startTime", System.currentTimeMillis());

        when(quizService.getQuizById(1L)).thenReturn(testQuizDTO);
        when(questionRepository.findByQuizId(1L)).thenReturn(Arrays.asList(testQuestion));
        when(answerRepository.findByQuestionIdAndIsCorrectTrue(1L)).thenReturn(Arrays.asList(testAnswer));
        when(quizRepository.findById(1L)).thenReturn(Optional.empty()); // Quiz not found

        mockMvc.perform(post("/submit/1")
                        .with(csrf())
                        .session(session)
                        .param("answer_1", "1"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Should handle empty answer")
    void submitQuiz_ShouldHandleEmptyAnswer() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("nickname", "Player");
        session.setAttribute("startTime", System.currentTimeMillis());

        when(quizService.getQuizById(1L)).thenReturn(testQuizDTO);
        when(questionRepository.findByQuizId(1L)).thenReturn(Arrays.asList(testQuestion));
        when(answerRepository.findByQuestionIdAndIsCorrectTrue(1L)).thenReturn(Arrays.asList(testAnswer));
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(quizResultRepository.save(any())).thenReturn(new QuizResult());

        mockMvc.perform(post("/submit/1")
                        .with(csrf())
                        .session(session)
                        .param("answer_1", "")) // Empty answer
                .andExpect(status().is3xxRedirection());
    }

    // ============ Submit Quiz Tests - Question Types ============

    @Test
    @DisplayName("Should handle TRUE_FALSE question type")
    void submitQuiz_ShouldHandleTrueFalseQuestion() throws Exception {
        testQuestion.setType(QuestionType.TRUE_FALSE);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("nickname", "Player");
        session.setAttribute("startTime", System.currentTimeMillis());

        when(quizService.getQuizById(1L)).thenReturn(testQuizDTO);
        when(questionRepository.findByQuizId(1L)).thenReturn(Arrays.asList(testQuestion));
        when(answerRepository.findByQuestionIdAndIsCorrectTrue(1L)).thenReturn(Arrays.asList(testAnswer));
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(quizResultRepository.save(any())).thenReturn(new QuizResult());

        mockMvc.perform(post("/submit/1")
                        .with(csrf())
                        .session(session)
                        .param("answer_1", "1"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Should handle DROPDOWN question type")
    void submitQuiz_ShouldHandleDropdownQuestion() throws Exception {
        testQuestion.setType(QuestionType.DROPDOWN);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("nickname", "Player");
        session.setAttribute("startTime", System.currentTimeMillis());

        when(quizService.getQuizById(1L)).thenReturn(testQuizDTO);
        when(questionRepository.findByQuizId(1L)).thenReturn(Arrays.asList(testQuestion));
        when(answerRepository.findByQuestionIdAndIsCorrectTrue(1L)).thenReturn(Arrays.asList(testAnswer));
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(quizResultRepository.save(any())).thenReturn(new QuizResult());

        mockMvc.perform(post("/submit/1")
                        .with(csrf())
                        .session(session)
                        .param("answer_1", "1"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Should handle MULTIPLE_CHOICE question type")
    void submitQuiz_ShouldHandleMultipleChoiceQuestion() throws Exception {
        testQuestion.setType(QuestionType.MULTIPLE_CHOICE);
        Answer answer2 = new Answer();
        answer2.setId(2L);
        answer2.setText("Also correct");
        answer2.setIsCorrect(true);
        answer2.setQuestion(testQuestion);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("nickname", "Player");
        session.setAttribute("startTime", System.currentTimeMillis());

        when(quizService.getQuizById(1L)).thenReturn(testQuizDTO);
        when(questionRepository.findByQuizId(1L)).thenReturn(Arrays.asList(testQuestion));
        when(answerRepository.findByQuestionIdAndIsCorrectTrue(1L)).thenReturn(Arrays.asList(testAnswer, answer2));
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(quizResultRepository.save(any())).thenReturn(new QuizResult());

        mockMvc.perform(post("/submit/1")
                        .with(csrf())
                        .session(session)
                        .param("answer_1", "1,2")) // Multiple answers
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Should handle SHORT_ANSWER question type")
    void submitQuiz_ShouldHandleShortAnswerQuestion() throws Exception {
        testQuestion.setType(QuestionType.SHORT_ANSWER);
        testAnswer.setText("Paris");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("nickname", "Player");
        session.setAttribute("startTime", System.currentTimeMillis());

        when(quizService.getQuizById(1L)).thenReturn(testQuizDTO);
        when(questionRepository.findByQuizId(1L)).thenReturn(Arrays.asList(testQuestion));
        when(answerRepository.findByQuestionIdAndIsCorrectTrue(1L)).thenReturn(Arrays.asList(testAnswer));
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(quizResultRepository.save(any())).thenReturn(new QuizResult());

        mockMvc.perform(post("/submit/1")
                        .with(csrf())
                        .session(session)
                        .param("answer_1", "Paris"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Should handle FILL_BLANK question type")
    void submitQuiz_ShouldHandleFillBlankQuestion() throws Exception {
        testQuestion.setType(QuestionType.FILL_BLANK);
        testAnswer.setText("answer");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("nickname", "Player");
        session.setAttribute("startTime", System.currentTimeMillis());

        when(quizService.getQuizById(1L)).thenReturn(testQuizDTO);
        when(questionRepository.findByQuizId(1L)).thenReturn(Arrays.asList(testQuestion));
        when(answerRepository.findByQuestionIdAndIsCorrectTrue(1L)).thenReturn(Arrays.asList(testAnswer));
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(quizResultRepository.save(any())).thenReturn(new QuizResult());

        mockMvc.perform(post("/submit/1")
                        .with(csrf())
                        .session(session)
                        .param("answer_1", "ANSWER")) // Case insensitive
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Should handle SORTING question type")
    void submitQuiz_ShouldHandleSortingQuestion() throws Exception {
        testQuestion.setType(QuestionType.SORTING);
        testAnswer.setOrderIndex(1);
        Answer answer2 = new Answer();
        answer2.setId(2L);
        answer2.setText("Second");
        answer2.setIsCorrect(true);
        answer2.setOrderIndex(2);
        answer2.setQuestion(testQuestion);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("nickname", "Player");
        session.setAttribute("startTime", System.currentTimeMillis());

        when(quizService.getQuizById(1L)).thenReturn(testQuizDTO);
        when(questionRepository.findByQuizId(1L)).thenReturn(Arrays.asList(testQuestion));
        when(answerRepository.findByQuestionIdAndIsCorrectTrue(1L)).thenReturn(Arrays.asList(testAnswer, answer2));
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(quizResultRepository.save(any())).thenReturn(new QuizResult());

        mockMvc.perform(post("/submit/1")
                        .with(csrf())
                        .session(session)
                        .param("answer_1", "1,2")) // Order matters
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Should handle MATCHING question type")
    void submitQuiz_ShouldHandleMatchingQuestion() throws Exception {
        testQuestion.setType(QuestionType.MATCHING);
        testAnswer.setOrderIndex(1);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("nickname", "Player");
        session.setAttribute("startTime", System.currentTimeMillis());

        when(quizService.getQuizById(1L)).thenReturn(testQuizDTO);
        when(questionRepository.findByQuizId(1L)).thenReturn(Arrays.asList(testQuestion));
        when(answerRepository.findByQuestionIdAndIsCorrectTrue(1L)).thenReturn(Arrays.asList(testAnswer));
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(quizResultRepository.save(any())).thenReturn(new QuizResult());

        mockMvc.perform(post("/submit/1")
                        .with(csrf())
                        .session(session)
                        .param("answer_1", "1"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Should handle invalid number format in single choice")
    void submitQuiz_ShouldHandleInvalidNumberFormat() throws Exception {
        testQuestion.setType(QuestionType.SINGLE_CHOICE);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("nickname", "Player");
        session.setAttribute("startTime", System.currentTimeMillis());

        when(quizService.getQuizById(1L)).thenReturn(testQuizDTO);
        when(questionRepository.findByQuizId(1L)).thenReturn(Arrays.asList(testQuestion));
        when(answerRepository.findByQuestionIdAndIsCorrectTrue(1L)).thenReturn(Arrays.asList(testAnswer));
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(quizResultRepository.save(any())).thenReturn(new QuizResult());

        mockMvc.perform(post("/submit/1")
                        .with(csrf())
                        .session(session)
                        .param("answer_1", "not-a-number"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Should handle invalid number format in sorting")
    void submitQuiz_ShouldHandleInvalidNumberFormatInSorting() throws Exception {
        testQuestion.setType(QuestionType.SORTING);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("nickname", "Player");
        session.setAttribute("startTime", System.currentTimeMillis());

        when(quizService.getQuizById(1L)).thenReturn(testQuizDTO);
        when(questionRepository.findByQuizId(1L)).thenReturn(Arrays.asList(testQuestion));
        when(answerRepository.findByQuestionIdAndIsCorrectTrue(1L)).thenReturn(Arrays.asList(testAnswer));
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(quizResultRepository.save(any())).thenReturn(new QuizResult());

        mockMvc.perform(post("/submit/1")
                        .with(csrf())
                        .session(session)
                        .param("answer_1", "a,b,c"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Should handle answer with null orderIndex in sorting")
    void submitQuiz_ShouldHandleNullOrderIndexInSorting() throws Exception {
        testQuestion.setType(QuestionType.SORTING);
        testAnswer.setOrderIndex(null); // Null order index
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("nickname", "Player");
        session.setAttribute("startTime", System.currentTimeMillis());

        when(quizService.getQuizById(1L)).thenReturn(testQuizDTO);
        when(questionRepository.findByQuizId(1L)).thenReturn(Arrays.asList(testQuestion));
        when(answerRepository.findByQuestionIdAndIsCorrectTrue(1L)).thenReturn(Arrays.asList(testAnswer));
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(quizResultRepository.save(any())).thenReturn(new QuizResult());

        mockMvc.perform(post("/submit/1")
                        .with(csrf())
                        .session(session)
                        .param("answer_1", "1"))
                .andExpect(status().is3xxRedirection());
    }

    // ============ Result Tests ============

    @Test
    @DisplayName("Should show result page")
    void showResult_ShouldReturnResultView() throws Exception {
        when(quizService.getQuizById(1L)).thenReturn(testQuizDTO);
        when(quizResultRepository.findTop10ByQuizIdOrderByScoreDesc(1L)).thenReturn(List.of());

        mockMvc.perform(get("/result/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("game/result"))
                .andExpect(model().attributeExists("quiz"))
                .andExpect(model().attributeExists("ranking"));
    }

    // ============ Ranking Tests ============

    @Test
    @DisplayName("Should show quiz ranking page")
    void showRanking_ShouldReturnRankingView() throws Exception {
        when(quizService.getQuizById(1L)).thenReturn(testQuizDTO);
        when(quizResultRepository.findByQuizIdOrderByScoreDesc(1L)).thenReturn(List.of());

        mockMvc.perform(get("/ranking/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("game/ranking"))
                .andExpect(model().attributeExists("quiz"))
                .andExpect(model().attributeExists("ranking"));
    }

    @Test
    @DisplayName("Should show global ranking page")
    void globalRanking_ShouldReturnGlobalRankingView() throws Exception {
        when(quizService.getAllQuizzes(any()))
                .thenReturn(new PageImpl<>(Arrays.asList(testQuizDTO), PageRequest.of(0, 100), 1));

        mockMvc.perform(get("/ranking"))
                .andExpect(status().isOk())
                .andExpect(view().name("game/global-ranking"))
                .andExpect(model().attributeExists("quizzes"));
    }
}

