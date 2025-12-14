package com.example.quizapp.repository;

import com.example.quizapp.entity.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA Repository for Answer entity.
 */
@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    // Find answers by question ID
    List<Answer> findByQuestionId(Long questionId);

    // Pagination support
    Page<Answer> findByQuestionId(Long questionId, Pageable pageable);

    // Find correct answers for a question
    List<Answer> findByQuestionIdAndIsCorrectTrue(Long questionId);

    // Find answers ordered by orderIndex
    List<Answer> findByQuestionIdOrderByOrderIndexAsc(Long questionId);

    // Count correct answers for a question
    @Query("SELECT COUNT(a) FROM Answer a WHERE a.question.id = :questionId AND a.isCorrect = true")
    Long countCorrectByQuestionId(@Param("questionId") Long questionId);
}
