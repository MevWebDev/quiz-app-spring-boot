package com.example.quizapp.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Quiz entity - main entity representing a quiz.
 */
@Entity
@Table(name = "quiz")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "time_limit")
    private Integer timeLimit; // in seconds, null means no limit

    @Column(name = "shuffle_questions")
    private Boolean shuffleQuestions = false;

    @Column(name = "shuffle_answers")
    private Boolean shuffleAnswers = false;

    @Column(name = "negative_points")
    private Boolean negativePoints = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizResult> results = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "quiz_categories",
        joinColumns = @JoinColumn(name = "quiz_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    // Constructors
    public Quiz() {
    }

    public Quiz(String title) {
        this.title = title;
    }

    public Quiz(String title, String description) {
        this.title = title;
        this.description = description;
    }

    // Helper methods
    public void addQuestion(Question question) {
        questions.add(question);
        question.setQuiz(this);
    }

    public void removeQuestion(Question question) {
        questions.remove(question);
        question.setQuiz(null);
    }

    public void addCategory(Category category) {
        categories.add(category);
        category.getQuizzes().add(this);
    }

    public void removeCategory(Category category) {
        categories.remove(category);
        category.getQuizzes().remove(this);
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Boolean getShuffleQuestions() {
        return shuffleQuestions;
    }

    public void setShuffleQuestions(Boolean shuffleQuestions) {
        this.shuffleQuestions = shuffleQuestions;
    }

    public Boolean getShuffleAnswers() {
        return shuffleAnswers;
    }

    public void setShuffleAnswers(Boolean shuffleAnswers) {
        this.shuffleAnswers = shuffleAnswers;
    }

    public Boolean getNegativePoints() {
        return negativePoints;
    }

    public void setNegativePoints(Boolean negativePoints) {
        this.negativePoints = negativePoints;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<QuizResult> getResults() {
        return results;
    }

    public void setResults(List<QuizResult> results) {
        this.results = results;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }
}
