package com.example.quizapp.dto;

import com.example.quizapp.entity.QuizResult;
import java.time.LocalDateTime;

/**
 * DTO for QuizResult entity (ranking).
 */
public class QuizResultDTO {

    private Long id;
    private String nickname;
    private Integer score;
    private Integer maxScore;
    private LocalDateTime completedAt;
    private Long quizId;
    private String quizTitle;

    public QuizResultDTO() {
    }

    public QuizResultDTO(QuizResult result) {
        this.id = result.getId();
        this.nickname = result.getNickname();
        this.score = result.getScore();
        this.maxScore = result.getMaxScore();
        this.completedAt = result.getCompletedAt();
        this.quizId = result.getQuiz() != null ? result.getQuiz().getId() : null;
        this.quizTitle = result.getQuiz() != null ? result.getQuiz().getTitle() : null;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public Integer getMaxScore() { return maxScore; }
    public void setMaxScore(Integer maxScore) { this.maxScore = maxScore; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public Long getQuizId() { return quizId; }
    public void setQuizId(Long quizId) { this.quizId = quizId; }

    public String getQuizTitle() { return quizTitle; }
    public void setQuizTitle(String quizTitle) { this.quizTitle = quizTitle; }
}
