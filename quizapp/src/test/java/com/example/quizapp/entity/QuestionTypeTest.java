package com.example.quizapp.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for QuestionType enum.
 */
class QuestionTypeTest {

    @Test
    @DisplayName("Should have all question types")
    void testAllTypes() {
        assertThat(QuestionType.values()).hasSize(8);
    }

    @Test
    @DisplayName("Should have SINGLE_CHOICE type")
    void testSingleChoice() {
        assertThat(QuestionType.SINGLE_CHOICE).isNotNull();
    }

    @Test
    @DisplayName("Should have MULTIPLE_CHOICE type")
    void testMultipleChoice() {
        assertThat(QuestionType.MULTIPLE_CHOICE).isNotNull();
    }

    @Test
    @DisplayName("Should have TRUE_FALSE type")
    void testTrueFalse() {
        assertThat(QuestionType.TRUE_FALSE).isNotNull();
    }

    @Test
    @DisplayName("Should have SHORT_ANSWER type")
    void testShortAnswer() {
        assertThat(QuestionType.SHORT_ANSWER).isNotNull();
    }

    @Test
    @DisplayName("Should have DROPDOWN type")
    void testDropdown() {
        assertThat(QuestionType.DROPDOWN).isNotNull();
    }

    @Test
    @DisplayName("Should have FILL_BLANK type")
    void testFillBlank() {
        assertThat(QuestionType.FILL_BLANK).isNotNull();
    }

    @Test
    @DisplayName("Should have SORTING type")
    void testSorting() {
        assertThat(QuestionType.SORTING).isNotNull();
    }

    @Test
    @DisplayName("Should have MATCHING type")
    void testMatching() {
        assertThat(QuestionType.MATCHING).isNotNull();
    }

    @Test
    @DisplayName("Should get type by name")
    void testValueOf() {
        assertThat(QuestionType.valueOf("SINGLE_CHOICE")).isEqualTo(QuestionType.SINGLE_CHOICE);
    }
}
