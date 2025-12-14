package com.example.quizapp.dto;

import com.example.quizapp.entity.Question;
import com.example.quizapp.entity.QuestionType;
import com.example.quizapp.entity.Quiz;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for QuestionDTO.
 */
class QuestionDTOTest {

    private QuestionDTO questionDTO;

    @BeforeEach
    void setUp() {
        questionDTO = new QuestionDTO();
    }

    @Test
    @DisplayName("Should set and get id")
    void testId() {
        questionDTO.setId(1L);
        assertThat(questionDTO.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should set and get text")
    void testText() {
        questionDTO.setText("What is Java?");
        assertThat(questionDTO.getText()).isEqualTo("What is Java?");
    }

    @Test
    @DisplayName("Should set and get type")
    void testType() {
        questionDTO.setType(QuestionType.MULTIPLE_CHOICE);
        assertThat(questionDTO.getType()).isEqualTo(QuestionType.MULTIPLE_CHOICE);
    }

    @Test
    @DisplayName("Should set and get points")
    void testPoints() {
        questionDTO.setPoints(5);
        assertThat(questionDTO.getPoints()).isEqualTo(5);
    }

    @Test
    @DisplayName("Should set and get orderIndex")
    void testOrderIndex() {
        questionDTO.setOrderIndex(3);
        assertThat(questionDTO.getOrderIndex()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should set and get quizId")
    void testQuizId() {
        questionDTO.setQuizId(10L);
        assertThat(questionDTO.getQuizId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Should create from entity")
    void testFromEntity() {
        Quiz quiz = new Quiz();
        quiz.setId(5L);

        Question question = new Question();
        question.setId(1L);
        question.setText("Test question");
        question.setType(QuestionType.TRUE_FALSE);
        question.setPoints(2);
        question.setOrderIndex(1);
        question.setQuiz(quiz);
        question.setAnswers(new ArrayList<>());

        QuestionDTO dto = new QuestionDTO(question);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getText()).isEqualTo("Test question");
        assertThat(dto.getType()).isEqualTo(QuestionType.TRUE_FALSE);
        assertThat(dto.getPoints()).isEqualTo(2);
        assertThat(dto.getOrderIndex()).isEqualTo(1);
        assertThat(dto.getQuizId()).isEqualTo(5L);
    }
}
