package com.example.quizapp.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Question entity.
 */
class QuestionTest {

    private Question question;

    @BeforeEach
    void setUp() {
        question = new Question();
    }

    @Test
    @DisplayName("Should set and get id")
    void testId() {
        question.setId(1L);
        assertThat(question.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should set and get text")
    void testText() {
        question.setText("What is Java?");
        assertThat(question.getText()).isEqualTo("What is Java?");
    }

    @Test
    @DisplayName("Should set and get type")
    void testType() {
        question.setType(QuestionType.SINGLE_CHOICE);
        assertThat(question.getType()).isEqualTo(QuestionType.SINGLE_CHOICE);
    }

    @Test
    @DisplayName("Should set and get points")
    void testPoints() {
        question.setPoints(5);
        assertThat(question.getPoints()).isEqualTo(5);
    }

    @Test
    @DisplayName("Should set and get orderIndex")
    void testOrderIndex() {
        question.setOrderIndex(3);
        assertThat(question.getOrderIndex()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should set and get quiz")
    void testQuiz() {
        Quiz quiz = new Quiz();
        quiz.setId(1L);
        question.setQuiz(quiz);
        assertThat(question.getQuiz()).isEqualTo(quiz);
    }

    @Test
    @DisplayName("Should set and get answers")
    void testAnswers() {
        question.setAnswers(new ArrayList<>());
        assertThat(question.getAnswers()).isEmpty();
    }

    @Test
    @DisplayName("Should create with constructor")
    void testConstructor() {
        Question q = new Question("Test?", QuestionType.TRUE_FALSE);
        assertThat(q.getText()).isEqualTo("Test?");
        assertThat(q.getType()).isEqualTo(QuestionType.TRUE_FALSE);
    }

    @Test
    @DisplayName("Should create with full constructor")
    void testFullConstructor() {
        Question q = new Question("Test?", QuestionType.MULTIPLE_CHOICE, 5);
        assertThat(q.getText()).isEqualTo("Test?");
        assertThat(q.getType()).isEqualTo(QuestionType.MULTIPLE_CHOICE);
        assertThat(q.getPoints()).isEqualTo(5);
    }

    @Test
    @DisplayName("Should add answer")
    void testAddAnswer() {
        Answer a = new Answer();
        question.addAnswer(a);
        assertThat(question.getAnswers()).contains(a);
        assertThat(a.getQuestion()).isEqualTo(question);
    }

    @Test
    @DisplayName("Should remove answer")
    void testRemoveAnswer() {
        Answer a = new Answer();
        question.addAnswer(a);
        question.removeAnswer(a);
        assertThat(question.getAnswers()).doesNotContain(a);
    }
}
