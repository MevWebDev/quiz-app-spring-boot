package com.example.quizapp.dao;

import com.example.quizapp.entity.Quiz;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for QuizRowMapper.
 */
class QuizRowMapperTest {

    private final QuizRowMapper rowMapper = new QuizRowMapper();

    @Test
    @DisplayName("Should map ResultSet to Quiz with time limit")
    void mapRow_ShouldMapAllFields() throws SQLException {
        // Given
        ResultSet rs = mock(ResultSet.class);
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);

        when(rs.getLong("id")).thenReturn(1L);
        when(rs.getString("title")).thenReturn("Test Quiz");
        when(rs.getString("description")).thenReturn("Description");
        when(rs.getInt("time_limit")).thenReturn(300);
        when(rs.wasNull()).thenReturn(false);  // time_limit is not null
        when(rs.getBoolean("shuffle_questions")).thenReturn(true);
        when(rs.getBoolean("shuffle_answers")).thenReturn(false);
        when(rs.getBoolean("negative_points")).thenReturn(true);
        when(rs.getTimestamp("created_at")).thenReturn(timestamp);
        when(rs.getTimestamp("updated_at")).thenReturn(timestamp);

        // When
        Quiz quiz = rowMapper.mapRow(rs, 1);

        // Then
        assertThat(quiz).isNotNull();
        assertThat(quiz.getId()).isEqualTo(1L);
        assertThat(quiz.getTitle()).isEqualTo("Test Quiz");
        assertThat(quiz.getDescription()).isEqualTo("Description");
        assertThat(quiz.getTimeLimit()).isEqualTo(300);
        assertThat(quiz.getShuffleQuestions()).isTrue();
        assertThat(quiz.getShuffleAnswers()).isFalse();
        assertThat(quiz.getNegativePoints()).isTrue();
        assertThat(quiz.getCreatedAt()).isEqualTo(now);
        assertThat(quiz.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should handle null time limit")
    void mapRow_ShouldHandleNullTimeLimit() throws SQLException {
        // Given
        ResultSet rs = mock(ResultSet.class);
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);

        when(rs.getLong("id")).thenReturn(2L);
        when(rs.getString("title")).thenReturn("Quiz No Limit");
        when(rs.getString("description")).thenReturn("Desc");
        when(rs.getInt("time_limit")).thenReturn(0);
        when(rs.wasNull()).thenReturn(true);  // time_limit is null
        when(rs.getBoolean("shuffle_questions")).thenReturn(false);
        when(rs.getBoolean("shuffle_answers")).thenReturn(false);
        when(rs.getBoolean("negative_points")).thenReturn(false);
        when(rs.getTimestamp("created_at")).thenReturn(timestamp);
        when(rs.getTimestamp("updated_at")).thenReturn(timestamp);

        // When
        Quiz quiz = rowMapper.mapRow(rs, 1);

        // Then
        assertThat(quiz).isNotNull();
        assertThat(quiz.getId()).isEqualTo(2L);
        assertThat(quiz.getTimeLimit()).isNull();
    }
}
