package com.example.quizapp.repository;

import com.example.quizapp.entity.Question;
import com.example.quizapp.entity.QuestionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA Repository for Question entity.
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    // Find questions by quiz ID
    List<Question> findByQuizId(Long quizId);

    // Pagination support for questions by quiz
    Page<Question> findByQuizId(Long quizId, Pageable pageable);

    // Find questions by type
    List<Question> findByType(QuestionType type);

    // Find questions by quiz ordered by orderIndex
    List<Question> findByQuizIdOrderByOrderIndexAsc(Long quizId);

    // Custom query - count questions in a quiz
    @Query("SELECT COUNT(q) FROM Question q WHERE q.quiz.id = :quizId")
    Long countByQuizId(@Param("quizId") Long quizId);
}
