package com.example.quizapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * QuizResult entity for storing quiz completion results (ranking).
 */
@Entity
@JsonIgnoreProperties({"quiz"})
@Table(name = "quiz_result")
public class QuizResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private Integer score;

    @Column(name = "max_score")
    private Integer maxScore;

    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    // Constructors
    public QuizResult() {
    }

    public QuizResult(String nickname, Integer score, Quiz quiz) {
        this.nickname = nickname;
        this.score = score;
        this.quiz = quiz;
        this.completedAt = LocalDateTime.now();
    }

    public QuizResult(String nickname, Integer score, Integer maxScore, Quiz quiz) {
        this.nickname = nickname;
        this.score = score;
        this.maxScore = maxScore;
        this.quiz = quiz;
        this.completedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Integer maxScore) {
        this.maxScore = maxScore;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }
}
