package com.example.quizapp.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Answer entity.
 */
class AnswerTest {

    private Answer answer;

    @BeforeEach
    void setUp() {
        answer = new Answer();
    }

    @Test
    @DisplayName("Should set and get id")
    void testId() {
        answer.setId(1L);
        assertThat(answer.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should set and get text")
    void testText() {
        answer.setText("This is an answer");
        assertThat(answer.getText()).isEqualTo("This is an answer");
    }

    @Test
    @DisplayName("Should set and get isCorrect")
    void testIsCorrect() {
        answer.setIsCorrect(true);
        assertThat(answer.getIsCorrect()).isTrue();
    }

    @Test
    @DisplayName("Should set and get orderIndex")
    void testOrderIndex() {
        answer.setOrderIndex(2);
        assertThat(answer.getOrderIndex()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should set and get question")
    void testQuestion() {
        Question question = new Question();
        question.setId(1L);
        answer.setQuestion(question);
        assertThat(answer.getQuestion()).isEqualTo(question);
    }

    @Test
    @DisplayName("Should create with constructor")
    void testConstructor() {
        Answer a = new Answer("Test", true);
        assertThat(a.getText()).isEqualTo("Test");
        assertThat(a.getIsCorrect()).isTrue();
    }

    @Test
    @DisplayName("Should create with full constructor")
    void testFullConstructor() {
        Answer a = new Answer("Test", true, 1);
        assertThat(a.getText()).isEqualTo("Test");
        assertThat(a.getIsCorrect()).isTrue();
        assertThat(a.getOrderIndex()).isEqualTo(1);
    }
}
