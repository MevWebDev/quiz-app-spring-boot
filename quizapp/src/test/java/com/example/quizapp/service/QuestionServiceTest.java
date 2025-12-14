package com.example.quizapp.service;

import com.example.quizapp.dto.QuestionDTO;
import com.example.quizapp.entity.Question;
import com.example.quizapp.entity.QuestionType;
import com.example.quizapp.entity.Quiz;
import com.example.quizapp.exception.ResourceNotFoundException;
import com.example.quizapp.repository.QuestionRepository;
import com.example.quizapp.repository.QuizRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for QuestionService.
 */
@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private QuizRepository quizRepository;

    @InjectMocks
    private QuestionService questionService;

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
    @DisplayName("Should get all questions with pagination")
    void getAllQuestions_ShouldReturnQuestions() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Question> page = new PageImpl<>(Arrays.asList(testQuestion));
        when(questionRepository.findAll(pageable)).thenReturn(page);

        // When
        Page<QuestionDTO> result = questionService.getAllQuestions(pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        verify(questionRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Should get questions by quiz id")
    void getQuestionsByQuiz_ShouldReturnQuestions() {
        // Given
        when(questionRepository.findByQuizId(1L)).thenReturn(Arrays.asList(testQuestion));

        // When
        List<QuestionDTO> result = questionService.getQuestionsByQuiz(1L, false);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getText()).isEqualTo("What is Java?");
        verify(questionRepository).findByQuizId(1L);
    }

    @Test
    @DisplayName("Should get questions by quiz id ordered")
    void getQuestionsByQuiz_Ordered_ShouldReturnOrderedQuestions() {
        // Given
        when(questionRepository.findByQuizIdOrderByOrderIndexAsc(1L)).thenReturn(Arrays.asList(testQuestion));

        // When
        List<QuestionDTO> result = questionService.getQuestionsByQuiz(1L, true);

        // Then
        assertThat(result).hasSize(1);
        verify(questionRepository).findByQuizIdOrderByOrderIndexAsc(1L);
    }

    @Test
    @DisplayName("Should get question by id")
    void getQuestionById_ShouldReturnQuestion() {
        // Given
        when(questionRepository.findById(1L)).thenReturn(Optional.of(testQuestion));

        // When
        QuestionDTO result = questionService.getQuestionById(1L);

        // Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getText()).isEqualTo("What is Java?");
        verify(questionRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when question not found")
    void getQuestionById_ShouldThrowException_WhenNotFound() {
        // Given
        when(questionRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> questionService.getQuestionById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(questionRepository).findById(999L);
    }

    @Test
    @DisplayName("Should create question")
    void createQuestion_ShouldSaveAndReturn() {
        // Given
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(questionRepository.save(any(Question.class))).thenReturn(testQuestion);

        QuestionDTO dto = new QuestionDTO();
        dto.setText("New question");
        dto.setType(QuestionType.TRUE_FALSE);
        dto.setPoints(3);
        dto.setQuizId(1L);

        // When
        QuestionDTO result = questionService.createQuestion(dto);

        // Then
        assertThat(result).isNotNull();
        verify(quizRepository).findById(1L);
        verify(questionRepository).save(any(Question.class));
    }

    @Test
    @DisplayName("Should update question")
    void updateQuestion_ShouldModifyAndReturn() {
        // Given
        when(questionRepository.findById(1L)).thenReturn(Optional.of(testQuestion));
        when(questionRepository.save(any(Question.class))).thenReturn(testQuestion);

        QuestionDTO dto = new QuestionDTO();
        dto.setText("Updated question");
        dto.setType(QuestionType.MULTIPLE_CHOICE);
        dto.setPoints(10);

        // When
        QuestionDTO result = questionService.updateQuestion(1L, dto);

        // Then
        assertThat(result).isNotNull();
        verify(questionRepository).findById(1L);
        verify(questionRepository).save(any(Question.class));
    }

    @Test
    @DisplayName("Should delete question")
    void deleteQuestion_ShouldRemoveQuestion() {
        // Given
        when(questionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(questionRepository).deleteById(1L);

        // When
        questionService.deleteQuestion(1L);

        // Then
        verify(questionRepository).existsById(1L);
        verify(questionRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent question")
    void deleteQuestion_ShouldThrowException_WhenNotFound() {
        // Given
        when(questionRepository.existsById(999L)).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> questionService.deleteQuestion(999L))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(questionRepository).existsById(999L);
        verify(questionRepository, never()).deleteById(any());
    }
}
