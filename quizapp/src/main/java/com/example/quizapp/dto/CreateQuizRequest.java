package com.example.quizapp.dto;

/**
 * Request DTO for creating a new quiz.
 */
public class CreateQuizRequest {

    private String title;
    private String description;
    private Integer timeLimit;
    private Boolean shuffleQuestions = false;
    private Boolean shuffleAnswers = false;
    private Boolean negativePoints = false;

    // Constructors
    public CreateQuizRequest() {
    }

    public CreateQuizRequest(String title, String description) {
        this.title = title;
        this.description = description;
    }

    // Getters and Setters
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
}
