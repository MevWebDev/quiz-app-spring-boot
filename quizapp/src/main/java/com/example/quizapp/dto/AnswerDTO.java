package com.example.quizapp.dto;

import com.example.quizapp.entity.Answer;

/**
 * DTO for Answer entity.
 */
public class AnswerDTO {

    private Long id;
    private String text;
    private Boolean isCorrect;
    private Integer orderIndex;
    private Long questionId;

    public AnswerDTO() {
    }

    public AnswerDTO(Answer answer) {
        this.id = answer.getId();
        this.text = answer.getText();
        this.isCorrect = answer.getIsCorrect();
        this.orderIndex = answer.getOrderIndex();
        this.questionId = answer.getQuestion() != null ? answer.getQuestion().getId() : null;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Boolean getIsCorrect() { return isCorrect; }
    public void setIsCorrect(Boolean isCorrect) { this.isCorrect = isCorrect; }

    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
}
