package com.example.quizapp.config;

import com.example.quizapp.entity.*;
import com.example.quizapp.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Initializes a comprehensive test quiz with all 8 question types.
 * Runs on application startup after DataInitializer.
 */
@Component
@Order(2) // Run after DataInitializer (which has default order)
public class QuizDataInitializer implements CommandLineRunner {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final CategoryRepository categoryRepository;

    public QuizDataInitializer(QuizRepository quizRepository,
                               QuestionRepository questionRepository,
                               AnswerRepository answerRepository,
                               CategoryRepository categoryRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {
        // Check if the quiz already exists to avoid duplicates
        if (quizRepository.findByTitleContaining("All Question Types").isEmpty()) {
            createAllQuestionTypesQuiz();
            System.out.println("Created 'All Question Types Test Quiz' with 8 questions");
        } else {
            System.out.println("'All Question Types Test Quiz' already exists - skipping creation");
        }
    }

    private void createAllQuestionTypesQuiz() {
        // Create or get the Test category
        Category testCategory = categoryRepository.findByName("Test")
                .orElseGet(() -> {
                    Category c = new Category();
                    c.setName("Test");
                    return categoryRepository.save(c);
                });

        // Create the quiz
        Quiz quiz = new Quiz();
        quiz.setTitle("All Question Types Test Quiz");
        quiz.setDescription("A comprehensive test quiz containing all 8 question types for manual testing.");
        quiz.setTimeLimit(600); // 10 minutes
        quiz.setShuffleQuestions(false);
        quiz.setShuffleAnswers(false);
        quiz.setNegativePoints(false);
        quiz.getCategories().add(testCategory);
        quiz = quizRepository.save(quiz);

        int orderIndex = 1;

        // 1. SINGLE_CHOICE
        Question q1 = createQuestion(quiz, "What is the capital of France?", QuestionType.SINGLE_CHOICE, 1, orderIndex++);
        createAnswer(q1, "London", false, 1);
        createAnswer(q1, "Paris", true, 2);
        createAnswer(q1, "Berlin", false, 3);
        createAnswer(q1, "Madrid", false, 4);

        // 2. MULTIPLE_CHOICE
        Question q2 = createQuestion(quiz, "Which of the following are programming languages? (Select all that apply)", QuestionType.MULTIPLE_CHOICE, 2, orderIndex++);
        createAnswer(q2, "Java", true, 1);
        createAnswer(q2, "Python", true, 2);
        createAnswer(q2, "HTML", false, 3);
        createAnswer(q2, "JavaScript", true, 4);

        // 3. TRUE_FALSE
        Question q3 = createQuestion(quiz, "The Earth is flat.", QuestionType.TRUE_FALSE, 1, orderIndex++);
        createAnswer(q3, "True", false, 1);
        createAnswer(q3, "False", true, 2);

        // 4. SHORT_ANSWER
        Question q4 = createQuestion(quiz, "What is 2 + 2? (Type the number)", QuestionType.SHORT_ANSWER, 1, orderIndex++);
        createAnswer(q4, "4", true, 1);

        // 5. DROPDOWN
        Question q5 = createQuestion(quiz, "Select the largest planet in our solar system:", QuestionType.DROPDOWN, 1, orderIndex++);
        createAnswer(q5, "Mars", false, 1);
        createAnswer(q5, "Earth", false, 2);
        createAnswer(q5, "Jupiter", true, 3);
        createAnswer(q5, "Neptune", false, 4);

        // 6. FILL_BLANK
        Question q6 = createQuestion(quiz, "Complete the sentence: The quick brown ___ jumps over the lazy dog.", QuestionType.FILL_BLANK, 1, orderIndex++);
        createAnswer(q6, "fox", true, 1);

        // 7. SORTING (put answers in correct order)
        Question q7 = createQuestion(quiz, "Sort the planets from closest to farthest from the Sun:", QuestionType.SORTING, 3, orderIndex++);
        createAnswer(q7, "Mercury", true, 1);
        createAnswer(q7, "Venus", true, 2);
        createAnswer(q7, "Earth", true, 3);
        createAnswer(q7, "Mars", true, 4);

        // 8. MATCHING
        Question q8 = createQuestion(quiz, "Match the countries with their capitals:", QuestionType.MATCHING, 2, orderIndex++);
        createAnswer(q8, "France - Paris", true, 1);
        createAnswer(q8, "Germany - Berlin", true, 2);
        createAnswer(q8, "Italy - Rome", true, 3);
        createAnswer(q8, "Spain - Madrid", true, 4);
    }

    private Question createQuestion(Quiz quiz, String text, QuestionType type, int points, int orderIndex) {
        Question question = new Question();
        question.setText(text);
        question.setType(type);
        question.setPoints(points);
        question.setOrderIndex(orderIndex);
        question.setQuiz(quiz);
        return questionRepository.save(question);
    }

    private void createAnswer(Question question, String text, boolean isCorrect, int orderIndex) {
        Answer answer = new Answer();
        answer.setText(text);
        answer.setIsCorrect(isCorrect);
        answer.setOrderIndex(orderIndex);
        answer.setQuestion(question);
        answerRepository.save(answer);
    }
}
