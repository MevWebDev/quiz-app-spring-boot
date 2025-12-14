package com.example.quizapp.dto;

import com.example.quizapp.entity.Question;
import com.example.quizapp.entity.QuestionType;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO for Question entity.
 */
public class QuestionDTO {

    private Long id;
    private String text;
    private QuestionType type;
    private Integer points;
    private Integer orderIndex;
    private Long quizId;
    private List<AnswerDTO> answers;

    public QuestionDTO() {
    }

    public QuestionDTO(Question question) {
        this.id = question.getId();
        this.text = question.getText();
        this.type = question.getType();
        this.points = question.getPoints();
        this.orderIndex = question.getOrderIndex();
        this.quizId = question.getQuiz() != null ? question.getQuiz().getId() : null;
        this.answers = question.getAnswers() != null 
            ? question.getAnswers().stream().map(AnswerDTO::new).collect(Collectors.toList())
            : List.of();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public QuestionType getType() { return type; }
    public void setType(QuestionType type) { this.type = type; }

    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }

    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }

    public Long getQuizId() { return quizId; }
    public void setQuizId(Long quizId) { this.quizId = quizId; }

    public List<AnswerDTO> getAnswers() { return answers; }
    public void setAnswers(List<AnswerDTO> answers) { this.answers = answers; }
}
