package com.example.quizapp.dao;

import com.example.quizapp.entity.Quiz;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * RowMapper implementation for Quiz entity.
 * Used with JdbcTemplate for mapping ResultSet rows to Quiz objects.
 */
@Component
public class QuizRowMapper implements RowMapper<Quiz> {

    @Override
    public Quiz mapRow(ResultSet rs, int rowNum) throws SQLException {
        Quiz quiz = new Quiz();
        quiz.setId(rs.getLong("id"));
        quiz.setTitle(rs.getString("title"));
        quiz.setDescription(rs.getString("description"));
        
        // Handle nullable integer
        int timeLimit = rs.getInt("time_limit");
        quiz.setTimeLimit(rs.wasNull() ? null : timeLimit);
        
        quiz.setShuffleQuestions(rs.getBoolean("shuffle_questions"));
        quiz.setShuffleAnswers(rs.getBoolean("shuffle_answers"));
        quiz.setNegativePoints(rs.getBoolean("negative_points"));
        
        // Handle timestamps
        java.sql.Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            quiz.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        java.sql.Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            quiz.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return quiz;
    }
}
