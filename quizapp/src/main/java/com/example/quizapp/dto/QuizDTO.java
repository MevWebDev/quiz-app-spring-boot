package com.example.quizapp.dto;

import com.example.quizapp.entity.Quiz;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DTO for Quiz entity responses.
 */
public class QuizDTO {

    private Long id;
    private String title;
    private String description;
    private Integer timeLimit;
    private Boolean shuffleQuestions;
    private Boolean shuffleAnswers;
    private Boolean negativePoints;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer questionCount;
    private Set<String> categories;

    // Constructors
    public QuizDTO() {
    }

    public QuizDTO(Quiz quiz) {
        this.id = quiz.getId();
        this.title = quiz.getTitle();
        this.description = quiz.getDescription();
        this.timeLimit = quiz.getTimeLimit();
        this.shuffleQuestions = quiz.getShuffleQuestions();
        this.shuffleAnswers = quiz.getShuffleAnswers();
        this.negativePoints = quiz.getNegativePoints();
        this.createdAt = quiz.getCreatedAt();
        this.updatedAt = quiz.getUpdatedAt();
        this.questionCount = quiz.getQuestions() != null ? quiz.getQuestions().size() : 0;
        this.categories = quiz.getCategories() != null 
            ? quiz.getCategories().stream().map(c -> c.getName()).collect(Collectors.toSet())
            : Set.of();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getTimeLimit() { return timeLimit; }
    public void setTimeLimit(Integer timeLimit) { this.timeLimit = timeLimit; }

    public Boolean getShuffleQuestions() { return shuffleQuestions; }
    public void setShuffleQuestions(Boolean shuffleQuestions) { this.shuffleQuestions = shuffleQuestions; }

    public Boolean getShuffleAnswers() { return shuffleAnswers; }
    public void setShuffleAnswers(Boolean shuffleAnswers) { this.shuffleAnswers = shuffleAnswers; }

    public Boolean getNegativePoints() { return negativePoints; }
    public void setNegativePoints(Boolean negativePoints) { this.negativePoints = negativePoints; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Integer getQuestionCount() { return questionCount; }
    public void setQuestionCount(Integer questionCount) { this.questionCount = questionCount; }

    public Set<String> getCategories() { return categories; }
    public void setCategories(Set<String> categories) { this.categories = categories; }
}
