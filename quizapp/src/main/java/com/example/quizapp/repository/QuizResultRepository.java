package com.example.quizapp.repository;

import com.example.quizapp.entity.QuizResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA Repository for QuizResult entity (ranking).
 */
@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {

    // Find results by quiz ID ordered by score (for ranking)
    List<QuizResult> findByQuizIdOrderByScoreDesc(Long quizId);

    // Pagination support for results
    Page<QuizResult> findByQuizId(Long quizId, Pageable pageable);

    // Find top N results for a quiz (ranking)
    List<QuizResult> findTop10ByQuizIdOrderByScoreDesc(Long quizId);

    // Find results by nickname
    List<QuizResult> findByNicknameContainingIgnoreCase(String nickname);

    // Custom query - get average score for a quiz
    @Query("SELECT AVG(r.score) FROM QuizResult r WHERE r.quiz.id = :quizId")
    Double getAverageScoreByQuizId(@Param("quizId") Long quizId);

    // Custom query - count attempts for a quiz
    @Query("SELECT COUNT(r) FROM QuizResult r WHERE r.quiz.id = :quizId")
    Long countAttemptsByQuizId(@Param("quizId") Long quizId);
}
