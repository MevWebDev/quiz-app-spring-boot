package com.example.quizapp.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Category entity.
 */
class CategoryTest {

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
    }

    @Test
    @DisplayName("Should set and get id")
    void testId() {
        category.setId(1L);
        assertThat(category.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should set and get name")
    void testName() {
        category.setName("Programming");
        assertThat(category.getName()).isEqualTo("Programming");
    }

    @Test
    @DisplayName("Should set and get description")
    void testDescription() {
        category.setDescription("Programming questions");
        assertThat(category.getDescription()).isEqualTo("Programming questions");
    }

    @Test
    @DisplayName("Should set and get quizzes")
    void testQuizzes() {
        category.setQuizzes(new HashSet<>());
        assertThat(category.getQuizzes()).isEmpty();
    }
}
