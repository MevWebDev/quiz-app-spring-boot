package com.example.quizapp.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ResourceNotFoundException.
 */
class ResourceNotFoundExceptionTest {

    @Test
    @DisplayName("Should create exception with resource name and id")
    void testExceptionMessage() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Quiz", 123L);
        assertThat(ex.getMessage()).contains("Quiz");
        assertThat(ex.getMessage()).contains("123");
    }

    @Test
    @DisplayName("Should be instance of RuntimeException")
    void testExceptionType() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Test", 1L);
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }
}
