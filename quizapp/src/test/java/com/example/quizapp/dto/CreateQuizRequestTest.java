package com.example.quizapp.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for CreateQuizRequest DTO.
 */
class CreateQuizRequestTest {

    private CreateQuizRequest request;

    @BeforeEach
    void setUp() {
        request = new CreateQuizRequest();
    }

    @Test
    @DisplayName("Should set and get title")
    void testTitle() {
        request.setTitle("New Quiz");
        assertThat(request.getTitle()).isEqualTo("New Quiz");
    }

    @Test
    @DisplayName("Should set and get description")
    void testDescription() {
        request.setDescription("New description");
        assertThat(request.getDescription()).isEqualTo("New description");
    }

    @Test
    @DisplayName("Should set and get timeLimit")
    void testTimeLimit() {
        request.setTimeLimit(600);
        assertThat(request.getTimeLimit()).isEqualTo(600);
    }

    @Test
    @DisplayName("Should set and get shuffleQuestions")
    void testShuffleQuestions() {
        request.setShuffleQuestions(true);
        assertThat(request.getShuffleQuestions()).isTrue();
    }

    @Test
    @DisplayName("Should set and get shuffleAnswers")
    void testShuffleAnswers() {
        request.setShuffleAnswers(true);
        assertThat(request.getShuffleAnswers()).isTrue();
    }

    @Test
    @DisplayName("Should set and get negativePoints")
    void testNegativePoints() {
        request.setNegativePoints(true);
        assertThat(request.getNegativePoints()).isTrue();
    }

    @Test
    @DisplayName("Should create with constructor")
    void testConstructorWithArgs() {
        CreateQuizRequest newRequest = new CreateQuizRequest("Title", "Description");
        assertThat(newRequest.getTitle()).isEqualTo("Title");
        assertThat(newRequest.getDescription()).isEqualTo("Description");
    }

    @Test
    @DisplayName("Should have default values")
    void testDefaultValues() {
        CreateQuizRequest newRequest = new CreateQuizRequest();
        assertThat(newRequest.getShuffleQuestions()).isFalse();
        assertThat(newRequest.getShuffleAnswers()).isFalse();
        assertThat(newRequest.getNegativePoints()).isFalse();
    }
}
