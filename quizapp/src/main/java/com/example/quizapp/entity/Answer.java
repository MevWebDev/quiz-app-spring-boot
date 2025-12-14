package com.example.quizapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

/**
 * Answer entity representing possible answers for a question.
 */
@Entity
@JsonIgnoreProperties({"question"})
@Table(name = "answer")
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect = false;

    @Column(name = "order_index")
    private Integer orderIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    // Constructors
    public Answer() {
    }

    public Answer(String text, Boolean isCorrect) {
        this.text = text;
        this.isCorrect = isCorrect;
    }

    public Answer(String text, Boolean isCorrect, Integer orderIndex) {
        this.text = text;
        this.isCorrect = isCorrect;
        this.orderIndex = orderIndex;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}
