package com.example.quizapp.dto;

import com.example.quizapp.entity.Quiz;
import com.example.quizapp.entity.QuizResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for QuizResultDTO.
 */
class QuizResultDTOTest {

    private QuizResultDTO resultDTO;

    @BeforeEach
    void setUp() {
        resultDTO = new QuizResultDTO();
    }

    @Test
    @DisplayName("Should set and get id")
    void testId() {
        resultDTO.setId(1L);
        assertThat(resultDTO.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should set and get nickname")
    void testNickname() {
        resultDTO.setNickname("John");
        assertThat(resultDTO.getNickname()).isEqualTo("John");
    }

    @Test
    @DisplayName("Should set and get score")
    void testScore() {
        resultDTO.setScore(85);
        assertThat(resultDTO.getScore()).isEqualTo(85);
    }

    @Test
    @DisplayName("Should set and get maxScore")
    void testMaxScore() {
        resultDTO.setMaxScore(100);
        assertThat(resultDTO.getMaxScore()).isEqualTo(100);
    }

    @Test
    @DisplayName("Should set and get completedAt")
    void testCompletedAt() {
        LocalDateTime now = LocalDateTime.now();
        resultDTO.setCompletedAt(now);
        assertThat(resultDTO.getCompletedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should set and get quizId")
    void testQuizId() {
        resultDTO.setQuizId(5L);
        assertThat(resultDTO.getQuizId()).isEqualTo(5L);
    }

    @Test
    @DisplayName("Should set and get quizTitle")
    void testQuizTitle() {
        resultDTO.setQuizTitle("Test Quiz");
        assertThat(resultDTO.getQuizTitle()).isEqualTo("Test Quiz");
    }

    @Test
    @DisplayName("Should create from entity")
    void testFromEntity() {
        Quiz quiz = new Quiz();
        quiz.setId(3L);
        quiz.setTitle("Quiz Title");

        QuizResult result = new QuizResult();
        result.setId(1L);
        result.setNickname("Alice");
        result.setScore(90);
        result.setMaxScore(100);
        result.setCompletedAt(LocalDateTime.now());
        result.setQuiz(quiz);

        QuizResultDTO dto = new QuizResultDTO(result);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getNickname()).isEqualTo("Alice");
        assertThat(dto.getScore()).isEqualTo(90);
        assertThat(dto.getMaxScore()).isEqualTo(100);
        assertThat(dto.getQuizId()).isEqualTo(3L);
        assertThat(dto.getQuizTitle()).isEqualTo("Quiz Title");
    }
}
