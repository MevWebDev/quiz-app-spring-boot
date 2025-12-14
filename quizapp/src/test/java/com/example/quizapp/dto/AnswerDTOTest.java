package com.example.quizapp.dto;

import com.example.quizapp.entity.Answer;
import com.example.quizapp.entity.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for AnswerDTO.
 */
class AnswerDTOTest {

    private AnswerDTO answerDTO;

    @BeforeEach
    void setUp() {
        answerDTO = new AnswerDTO();
    }

    @Test
    @DisplayName("Should set and get id")
    void testId() {
        answerDTO.setId(1L);
        assertThat(answerDTO.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should set and get text")
    void testText() {
        answerDTO.setText("Answer text");
        assertThat(answerDTO.getText()).isEqualTo("Answer text");
    }

    @Test
    @DisplayName("Should set and get isCorrect")
    void testIsCorrect() {
        answerDTO.setIsCorrect(true);
        assertThat(answerDTO.getIsCorrect()).isTrue();
    }

    @Test
    @DisplayName("Should set and get orderIndex")
    void testOrderIndex() {
        answerDTO.setOrderIndex(3);
        assertThat(answerDTO.getOrderIndex()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should set and get questionId")
    void testQuestionId() {
        answerDTO.setQuestionId(5L);
        assertThat(answerDTO.getQuestionId()).isEqualTo(5L);
    }

    @Test
    @DisplayName("Should create from entity")
    void testFromEntity() {
        Question question = new Question();
        question.setId(10L);

        Answer answer = new Answer();
        answer.setId(1L);
        answer.setText("Entity answer");
        answer.setIsCorrect(true);
        answer.setOrderIndex(2);
        answer.setQuestion(question);

        AnswerDTO dto = new AnswerDTO(answer);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getText()).isEqualTo("Entity answer");
        assertThat(dto.getIsCorrect()).isTrue();
        assertThat(dto.getOrderIndex()).isEqualTo(2);
        assertThat(dto.getQuestionId()).isEqualTo(10L);
    }
}
