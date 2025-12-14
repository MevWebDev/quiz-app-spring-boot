package com.example.quizapp.repository;

import com.example.quizapp.entity.Quiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA Repository for Quiz entity.
 */
@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    // Custom query method - findBy...
    List<Quiz> findByTitleContainingIgnoreCase(String title);

    // Pagination support
    Page<Quiz> findAll(Pageable pageable);

    // Custom @Query - find quizzes by category name
    @Query("SELECT DISTINCT q FROM Quiz q JOIN q.categories c WHERE c.name = :categoryName")
    List<Quiz> findByCategory(@Param("categoryName") String categoryName);

    // Find quizzes with time limit
    List<Quiz> findByTimeLimitIsNotNull();

    // Find quizzes with negative points enabled
    List<Quiz> findByNegativePointsTrue();
}
