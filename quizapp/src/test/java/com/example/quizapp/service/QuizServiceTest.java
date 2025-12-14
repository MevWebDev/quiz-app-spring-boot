package com.example.quizapp.service;

import com.example.quizapp.dto.CreateQuizRequest;
import com.example.quizapp.dto.QuizDTO;
import com.example.quizapp.entity.Quiz;
import com.example.quizapp.exception.ResourceNotFoundException;
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
 * Unit tests for QuizService using Mockito.
 * Demonstrates @Mock, @InjectMocks, when().thenReturn(), verify().
 */
@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @InjectMocks
    private QuizService quizService;

    private Quiz testQuiz;
    private CreateQuizRequest createRequest;

    @BeforeEach
    void setUp() {
        testQuiz = new Quiz();
        testQuiz.setId(1L);
        testQuiz.setTitle("Test Quiz");
        testQuiz.setDescription("Test Description");
        testQuiz.setTimeLimit(300);

        createRequest = new CreateQuizRequest();
        createRequest.setTitle("New Quiz");
        createRequest.setDescription("New Description");
        createRequest.setTimeLimit(600);
    }

    @Test
    @DisplayName("Should get all quizzes with pagination")
    void getAllQuizzes_ShouldReturnPageOfQuizzes() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Quiz> quizPage = new PageImpl<>(Arrays.asList(testQuiz));
        when(quizRepository.findAll(pageable)).thenReturn(quizPage);

        // When
        Page<QuizDTO> result = quizService.getAllQuizzes(pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Test Quiz");
        verify(quizRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Should get quiz by ID successfully")
    void getQuizById_ShouldReturnQuiz_WhenExists() {
        // Given
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));

        // When
        QuizDTO result = quizService.getQuizById(1L);

        // Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Test Quiz");
        verify(quizRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when quiz not found")
    void getQuizById_ShouldThrowException_WhenNotFound() {
        // Given
        when(quizRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> quizService.getQuizById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
        verify(quizRepository).findById(999L);
    }

    @Test
    @DisplayName("Should search quizzes by title")
    void searchQuizzes_ShouldReturnMatchingQuizzes() {
        // Given
        when(quizRepository.findByTitleContainingIgnoreCase("test"))
                .thenReturn(Arrays.asList(testQuiz));

        // When
        List<QuizDTO> result = quizService.searchQuizzes("test");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Test Quiz");
        verify(quizRepository).findByTitleContainingIgnoreCase("test");
    }

    @Test
    @DisplayName("Should create quiz successfully")
    void createQuiz_ShouldSaveAndReturnQuiz() {
        // Given
        Quiz savedQuiz = new Quiz();
        savedQuiz.setId(2L);
        savedQuiz.setTitle("New Quiz");
        savedQuiz.setDescription("New Description");
        when(quizRepository.save(any(Quiz.class))).thenReturn(savedQuiz);

        // When
        QuizDTO result = quizService.createQuiz(createRequest);

        // Then
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getTitle()).isEqualTo("New Quiz");
        verify(quizRepository).save(any(Quiz.class));
    }

    @Test
    @DisplayName("Should update quiz successfully")
    void updateQuiz_ShouldModifyAndReturnQuiz() {
        // Given
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(quizRepository.save(any(Quiz.class))).thenReturn(testQuiz);

        // When
        QuizDTO result = quizService.updateQuiz(1L, createRequest);

        // Then
        verify(quizRepository).findById(1L);
        verify(quizRepository).save(any(Quiz.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent quiz")
    void updateQuiz_ShouldThrowException_WhenNotFound() {
        // Given
        when(quizRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> quizService.updateQuiz(999L, createRequest))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(quizRepository).findById(999L);
        verify(quizRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete quiz successfully")
    void deleteQuiz_ShouldRemoveQuiz_WhenExists() {
        // Given
        when(quizRepository.existsById(1L)).thenReturn(true);
        doNothing().when(quizRepository).deleteById(1L);

        // When
        quizService.deleteQuiz(1L);

        // Then
        verify(quizRepository).existsById(1L);
        verify(quizRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent quiz")
    void deleteQuiz_ShouldThrowException_WhenNotFound() {
        // Given
        when(quizRepository.existsById(999L)).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> quizService.deleteQuiz(999L))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(quizRepository).existsById(999L);
        verify(quizRepository, never()).deleteById(any());
    }
}
