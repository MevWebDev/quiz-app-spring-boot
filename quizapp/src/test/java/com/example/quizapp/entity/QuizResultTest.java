package com.example.quizapp.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for QuizResult entity.
 */
class QuizResultTest {

    private QuizResult quizResult;

    @BeforeEach
    void setUp() {
        quizResult = new QuizResult();
    }

    @Test
    @DisplayName("Should set and get id")
    void testId() {
        quizResult.setId(1L);
        assertThat(quizResult.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should set and get nickname")
    void testNickname() {
        quizResult.setNickname("John");
        assertThat(quizResult.getNickname()).isEqualTo("John");
    }

    @Test
    @DisplayName("Should set and get score")
    void testScore() {
        quizResult.setScore(85);
        assertThat(quizResult.getScore()).isEqualTo(85);
    }

    @Test
    @DisplayName("Should set and get maxScore")
    void testMaxScore() {
        quizResult.setMaxScore(100);
        assertThat(quizResult.getMaxScore()).isEqualTo(100);
    }

    @Test
    @DisplayName("Should set and get completedAt")
    void testCompletedAt() {
        LocalDateTime now = LocalDateTime.now();
        quizResult.setCompletedAt(now);
        assertThat(quizResult.getCompletedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should set and get quiz")
    void testQuiz() {
        Quiz quiz = new Quiz();
        quiz.setId(1L);
        quizResult.setQuiz(quiz);
        assertThat(quizResult.getQuiz()).isEqualTo(quiz);
    }

    @Test
    @DisplayName("Should create with constructor")
    void testConstructor() {
        Quiz quiz = new Quiz();
        QuizResult result = new QuizResult("Alice", 90, quiz);
        assertThat(result.getNickname()).isEqualTo("Alice");
        assertThat(result.getScore()).isEqualTo(90);
    }

    @Test
    @DisplayName("Should create with full constructor")
    void testFullConstructor() {
        Quiz quiz = new Quiz();
        QuizResult result = new QuizResult("Bob", 80, 100, quiz);
        assertThat(result.getNickname()).isEqualTo("Bob");
        assertThat(result.getScore()).isEqualTo(80);
        assertThat(result.getMaxScore()).isEqualTo(100);
    }
}
