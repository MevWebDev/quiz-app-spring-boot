package com.example.quizapp.service;

import com.example.quizapp.dto.CreateQuizRequest;
import com.example.quizapp.dto.QuizDTO;
import com.example.quizapp.entity.Quiz;
import com.example.quizapp.exception.ResourceNotFoundException;
import com.example.quizapp.repository.QuizRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer for Quiz operations.
 * Demonstrates @Service, constructor injection, and @Transactional.
 */
@Service
public class QuizService {

    private final QuizRepository quizRepository;

    // Constructor injection
    public QuizService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Transactional(readOnly = true)
    public Page<QuizDTO> getAllQuizzes(Pageable pageable) {
        return quizRepository.findAll(pageable).map(QuizDTO::new);
    }

    @Transactional(readOnly = true)
    public QuizDTO getQuizById(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", id));
        return new QuizDTO(quiz);
    }

    @Transactional(readOnly = true)
    public List<QuizDTO> searchQuizzes(String title) {
        return quizRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(QuizDTO::new)
                .toList();
    }

    @Transactional(readOnly = false)
    public QuizDTO createQuiz(CreateQuizRequest request) {
        Quiz quiz = new Quiz();
        mapRequestToEntity(request, quiz);
        Quiz saved = quizRepository.save(quiz);
        return new QuizDTO(saved);
    }

    @Transactional(readOnly = false)
    public QuizDTO updateQuiz(Long id, CreateQuizRequest request) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", id));
        mapRequestToEntity(request, quiz);
        Quiz updated = quizRepository.save(quiz);
        return new QuizDTO(updated);
    }

    @Transactional(readOnly = false)
    public void deleteQuiz(Long id) {
        if (!quizRepository.existsById(id)) {
            throw new ResourceNotFoundException("Quiz", id);
        }
        quizRepository.deleteById(id);
    }

    // Entity ⇄ DTO mapping helper
    private void mapRequestToEntity(CreateQuizRequest request, Quiz quiz) {
        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setTimeLimit(request.getTimeLimit());
        quiz.setShuffleQuestions(request.getShuffleQuestions());
        quiz.setShuffleAnswers(request.getShuffleAnswers());
        quiz.setNegativePoints(request.getNegativePoints());
    }
}
