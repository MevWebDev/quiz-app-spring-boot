package com.example.quizapp.dto;

import com.example.quizapp.entity.Quiz;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for QuizDTO.
 */
class QuizDTOTest {

    private QuizDTO quizDTO;

    @BeforeEach
    void setUp() {
        quizDTO = new QuizDTO();
    }

    @Test
    @DisplayName("Should set and get id")
    void testId() {
        quizDTO.setId(1L);
        assertThat(quizDTO.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should set and get title")
    void testTitle() {
        quizDTO.setTitle("Test Quiz");
        assertThat(quizDTO.getTitle()).isEqualTo("Test Quiz");
    }

    @Test
    @DisplayName("Should set and get description")
    void testDescription() {
        quizDTO.setDescription("Test description");
        assertThat(quizDTO.getDescription()).isEqualTo("Test description");
    }

    @Test
    @DisplayName("Should set and get timeLimit")
    void testTimeLimit() {
        quizDTO.setTimeLimit(300);
        assertThat(quizDTO.getTimeLimit()).isEqualTo(300);
    }

    @Test
    @DisplayName("Should set and get shuffleQuestions")
    void testShuffleQuestions() {
        quizDTO.setShuffleQuestions(true);
        assertThat(quizDTO.getShuffleQuestions()).isTrue();
    }

    @Test
    @DisplayName("Should set and get shuffleAnswers")
    void testShuffleAnswers() {
        quizDTO.setShuffleAnswers(true);
        assertThat(quizDTO.getShuffleAnswers()).isTrue();
    }

    @Test
    @DisplayName("Should set and get negativePoints")
    void testNegativePoints() {
        quizDTO.setNegativePoints(true);
        assertThat(quizDTO.getNegativePoints()).isTrue();
    }

    @Test
    @DisplayName("Should set and get questionCount")
    void testQuestionCount() {
        quizDTO.setQuestionCount(10);
        assertThat(quizDTO.getQuestionCount()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should set and get createdAt")
    void testCreatedAt() {
        LocalDateTime now = LocalDateTime.now();
        quizDTO.setCreatedAt(now);
        assertThat(quizDTO.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should create from entity")
    void testFromEntity() {
        Quiz quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Entity Quiz");
        quiz.setDescription("Entity description");
        quiz.setTimeLimit(600);
        quiz.setShuffleQuestions(true);
        quiz.setShuffleAnswers(false);
        quiz.setNegativePoints(true);
        quiz.setQuestions(new ArrayList<>());
        quiz.setCategories(new HashSet<>());
        quiz.setCreatedAt(LocalDateTime.now());

        QuizDTO dto = new QuizDTO(quiz);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getTitle()).isEqualTo("Entity Quiz");
        assertThat(dto.getDescription()).isEqualTo("Entity description");
        assertThat(dto.getTimeLimit()).isEqualTo(600);
        assertThat(dto.getShuffleQuestions()).isTrue();
        assertThat(dto.getShuffleAnswers()).isFalse();
        assertThat(dto.getNegativePoints()).isTrue();
        assertThat(dto.getQuestionCount()).isEqualTo(0);
    }
}
