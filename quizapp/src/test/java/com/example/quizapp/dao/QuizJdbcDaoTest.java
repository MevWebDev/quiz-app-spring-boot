package com.example.quizapp.dao;

import com.example.quizapp.entity.Quiz;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for QuizJdbcDao using Mockito.
 */
@ExtendWith(MockitoExtension.class)
class QuizJdbcDaoTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private QuizRowMapper quizRowMapper;

    @InjectMocks
    private QuizJdbcDao quizJdbcDao;

    private Quiz testQuiz;

    @BeforeEach
    void setUp() {
        testQuiz = new Quiz();
        testQuiz.setId(1L);
        testQuiz.setTitle("Test Quiz");
        testQuiz.setDescription("Test Description");
    }

    @Test
    @DisplayName("Should find all quizzes")
    void findAll_ShouldReturnAllQuizzes() {
        // Given
        when(jdbcTemplate.query(anyString(), eq(quizRowMapper)))
                .thenReturn(Arrays.asList(testQuiz));

        // When
        List<Quiz> result = quizJdbcDao.findAll();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Test Quiz");
        verify(jdbcTemplate).query(anyString(), eq(quizRowMapper));
    }

    @Test
    @DisplayName("Should find quiz by id")
    void findById_ShouldReturnQuiz() {
        // Given
        when(jdbcTemplate.query(anyString(), eq(quizRowMapper), eq(1L)))
                .thenReturn(Arrays.asList(testQuiz));

        // When
        Optional<Quiz> result = quizJdbcDao.findById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Test Quiz");
        verify(jdbcTemplate).query(anyString(), eq(quizRowMapper), eq(1L));
    }

    @Test
    @DisplayName("Should return empty when quiz not found")
    void findById_ShouldReturnEmpty_WhenNotFound() {
        // Given
        when(jdbcTemplate.query(anyString(), eq(quizRowMapper), eq(999L)))
                .thenReturn(Arrays.asList());

        // When
        Optional<Quiz> result = quizJdbcDao.findById(999L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should insert quiz")
    void insert_ShouldInsertNewQuiz() {
        // Given
        Quiz newQuiz = new Quiz();
        newQuiz.setTitle("New Quiz");
        newQuiz.setDescription("New Description");
        newQuiz.setShuffleQuestions(false);
        newQuiz.setShuffleAnswers(false);
        newQuiz.setNegativePoints(false);
        
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any(), any(), any()))
                .thenReturn(1);

        // When
        int result = quizJdbcDao.insert(newQuiz);

        // Then
        assertThat(result).isEqualTo(1);
        verify(jdbcTemplate).update(anyString(), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should update quiz title")
    void updateTitle_ShouldUpdateQuiz() {
        // Given
        when(jdbcTemplate.update(anyString(), eq("New Title"), eq(1L))).thenReturn(1);

        // When
        int result = quizJdbcDao.updateTitle(1L, "New Title");

        // Then
        assertThat(result).isEqualTo(1);
        verify(jdbcTemplate).update(anyString(), eq("New Title"), eq(1L));
    }

    @Test
    @DisplayName("Should update quiz settings")
    void updateSettings_ShouldUpdateSettings() {
        // Given
        when(jdbcTemplate.update(anyString(), eq(true), eq(false), eq(true), eq(1L))).thenReturn(1);

        // When
        int result = quizJdbcDao.updateSettings(1L, true, false, true);

        // Then
        assertThat(result).isEqualTo(1);
        verify(jdbcTemplate).update(anyString(), eq(true), eq(false), eq(true), eq(1L));
    }

    @Test
    @DisplayName("Should update quiz time limit")
    void updateTimeLimit_ShouldUpdateTimeLimit() {
        // Given
        when(jdbcTemplate.update(anyString(), eq(600), eq(1L))).thenReturn(1);

        // When
        int result = quizJdbcDao.updateTimeLimit(1L, 600);

        // Then
        assertThat(result).isEqualTo(1);
        verify(jdbcTemplate).update(anyString(), eq(600), eq(1L));
    }

    @Test
    @DisplayName("Should delete quiz by id")
    void deleteById_ShouldDeleteQuiz() {
        // Given
        when(jdbcTemplate.update(anyString(), eq(1L))).thenReturn(1);

        // When
        int result = quizJdbcDao.deleteById(1L);

        // Then
        assertThat(result).isEqualTo(1);
        verify(jdbcTemplate).update(anyString(), eq(1L));
    }

    @Test
    @DisplayName("Should delete all quizzes")
    void deleteAll_ShouldDeleteAllQuizzes() {
        // Given
        when(jdbcTemplate.update(anyString())).thenReturn(5);

        // When
        int result = quizJdbcDao.deleteAll();

        // Then
        assertThat(result).isEqualTo(5);
        verify(jdbcTemplate).update(anyString());
    }

    @Test
    @DisplayName("Should find quizzes by title")
    void findByTitleContaining_ShouldReturnMatchingQuizzes() {
        // Given
        when(jdbcTemplate.query(anyString(), eq(quizRowMapper), anyString()))
                .thenReturn(Arrays.asList(testQuiz));

        // When
        List<Quiz> result = quizJdbcDao.findByTitleContaining("Test");

        // Then
        assertThat(result).hasSize(1);
        verify(jdbcTemplate).query(anyString(), eq(quizRowMapper), anyString());
    }

    @Test
    @DisplayName("Should find quizzes with time limit")
    void findWithTimeLimit_ShouldReturnQuizzes() {
        // Given
        when(jdbcTemplate.query(anyString(), eq(quizRowMapper)))
                .thenReturn(Arrays.asList(testQuiz));

        // When
        List<Quiz> result = quizJdbcDao.findWithTimeLimit();

        // Then
        assertThat(result).hasSize(1);
        verify(jdbcTemplate).query(anyString(), eq(quizRowMapper));
    }

    @Test
    @DisplayName("Should count quizzes")
    void count_ShouldReturnQuizCount() {
        // Given
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class))).thenReturn(5L);

        // When
        Long result = quizJdbcDao.count();

        // Then
        assertThat(result).isEqualTo(5L);
        verify(jdbcTemplate).queryForObject(anyString(), eq(Long.class));
    }
}
