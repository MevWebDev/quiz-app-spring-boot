package com.example.quizapp.dao;

import com.example.quizapp.entity.Quiz;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JdbcTemplate-based DAO for Quiz operations.
 * Demonstrates usage of JdbcTemplate with query() and update() methods.
 */
@Repository
public class QuizJdbcDao {

    private final JdbcTemplate jdbcTemplate;
    private final QuizRowMapper quizRowMapper;

    // Constructor injection
    public QuizJdbcDao(JdbcTemplate jdbcTemplate, QuizRowMapper quizRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.quizRowMapper = quizRowMapper;
    }

    // ==================== SELECT Operations with query() ====================

    /**
     * Find all quizzes using JdbcTemplate.
     */
    public List<Quiz> findAll() {
        String sql = "SELECT * FROM quiz ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, quizRowMapper);
    }

    /**
     * Find quiz by ID.
     */
    public Optional<Quiz> findById(Long id) {
        String sql = "SELECT * FROM quiz WHERE id = ?";
        List<Quiz> results = jdbcTemplate.query(sql, quizRowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /**
     * Find quizzes by title (partial match).
     */
    public List<Quiz> findByTitleContaining(String title) {
        String sql = "SELECT * FROM quiz WHERE LOWER(title) LIKE LOWER(?)";
        return jdbcTemplate.query(sql, quizRowMapper, "%" + title + "%");
    }

    /**
     * Find quizzes with time limit.
     */
    public List<Quiz> findWithTimeLimit() {
        String sql = "SELECT * FROM quiz WHERE time_limit IS NOT NULL";
        return jdbcTemplate.query(sql, quizRowMapper);
    }

    /**
     * Count total quizzes.
     */
    public Long count() {
        String sql = "SELECT COUNT(*) FROM quiz";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    // ==================== INSERT/UPDATE/DELETE with update() ====================

    /**
     * Insert a new quiz.
     */
    public int insert(Quiz quiz) {
        String sql = """
            INSERT INTO quiz (title, description, time_limit, shuffle_questions, 
                            shuffle_answers, negative_points, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            """;
        return jdbcTemplate.update(sql,
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.getTimeLimit(),
                quiz.getShuffleQuestions(),
                quiz.getShuffleAnswers(),
                quiz.getNegativePoints()
        );
    }

    /**
     * Update quiz title.
     */
    public int updateTitle(Long id, String newTitle) {
        String sql = "UPDATE quiz SET title = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        return jdbcTemplate.update(sql, newTitle, id);
    }

    /**
     * Update quiz settings.
     */
    public int updateSettings(Long id, Boolean shuffleQuestions, Boolean shuffleAnswers, Boolean negativePoints) {
        String sql = """
            UPDATE quiz 
            SET shuffle_questions = ?, shuffle_answers = ?, negative_points = ?, 
                updated_at = CURRENT_TIMESTAMP 
            WHERE id = ?
            """;
        return jdbcTemplate.update(sql, shuffleQuestions, shuffleAnswers, negativePoints, id);
    }

    /**
     * Update quiz time limit.
     */
    public int updateTimeLimit(Long id, Integer timeLimit) {
        String sql = "UPDATE quiz SET time_limit = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        return jdbcTemplate.update(sql, timeLimit, id);
    }

    /**
     * Delete quiz by ID.
     */
    public int deleteById(Long id) {
        String sql = "DELETE FROM quiz WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    /**
     * Delete all quizzes.
     */
    public int deleteAll() {
        String sql = "DELETE FROM quiz";
        return jdbcTemplate.update(sql);
    }
}
