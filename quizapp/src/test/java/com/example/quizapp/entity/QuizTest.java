package com.example.quizapp.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Quiz entity - testing getters/setters.
 */
class QuizTest {

    private Quiz quiz;

    @BeforeEach
    void setUp() {
        quiz = new Quiz();
    }

    @Test
    @DisplayName("Should set and get id")
    void testId() {
        quiz.setId(1L);
        assertThat(quiz.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should set and get title")
    void testTitle() {
        quiz.setTitle("Test Quiz");
        assertThat(quiz.getTitle()).isEqualTo("Test Quiz");
    }

    @Test
    @DisplayName("Should set and get description")
    void testDescription() {
        quiz.setDescription("A test description");
        assertThat(quiz.getDescription()).isEqualTo("A test description");
    }

    @Test
    @DisplayName("Should set and get timeLimit")
    void testTimeLimit() {
        quiz.setTimeLimit(300);
        assertThat(quiz.getTimeLimit()).isEqualTo(300);
    }

    @Test
    @DisplayName("Should set and get shuffleQuestions")
    void testShuffleQuestions() {
        quiz.setShuffleQuestions(true);
        assertThat(quiz.getShuffleQuestions()).isTrue();
    }

    @Test
    @DisplayName("Should set and get shuffleAnswers")
    void testShuffleAnswers() {
        quiz.setShuffleAnswers(true);
        assertThat(quiz.getShuffleAnswers()).isTrue();
    }

    @Test
    @DisplayName("Should set and get negativePoints")
    void testNegativePoints() {
        quiz.setNegativePoints(true);
        assertThat(quiz.getNegativePoints()).isTrue();
    }

    @Test
    @DisplayName("Should set and get createdAt")
    void testCreatedAt() {
        LocalDateTime now = LocalDateTime.now();
        quiz.setCreatedAt(now);
        assertThat(quiz.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should set and get updatedAt")
    void testUpdatedAt() {
        LocalDateTime now = LocalDateTime.now();
        quiz.setUpdatedAt(now);
        assertThat(quiz.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should set and get questions")
    void testQuestions() {
        quiz.setQuestions(new ArrayList<>());
        assertThat(quiz.getQuestions()).isEmpty();
    }

    @Test
    @DisplayName("Should set and get categories")
    void testCategories() {
        quiz.setCategories(new HashSet<>());
        assertThat(quiz.getCategories()).isEmpty();
    }

    @Test
    @DisplayName("Should set and get results")
    void testResults() {
        quiz.setResults(new ArrayList<>());
        assertThat(quiz.getResults()).isEmpty();
    }

    @Test
    @DisplayName("Should create with title constructor")
    void testTitleConstructor() {
        Quiz q = new Quiz("My Quiz");
        assertThat(q.getTitle()).isEqualTo("My Quiz");
    }

    @Test
    @DisplayName("Should create with full constructor")
    void testFullConstructor() {
        Quiz q = new Quiz("My Quiz", "Description");
        assertThat(q.getTitle()).isEqualTo("My Quiz");
        assertThat(q.getDescription()).isEqualTo("Description");
    }

    @Test
    @DisplayName("Should add question")
    void testAddQuestion() {
        Question q = new Question();
        quiz.addQuestion(q);
        assertThat(quiz.getQuestions()).contains(q);
        assertThat(q.getQuiz()).isEqualTo(quiz);
    }

    @Test
    @DisplayName("Should remove question")
    void testRemoveQuestion() {
        Question q = new Question();
        quiz.addQuestion(q);
        quiz.removeQuestion(q);
        assertThat(quiz.getQuestions()).doesNotContain(q);
    }
}
