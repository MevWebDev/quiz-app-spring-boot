package com.example.quizapp.controller;

import com.example.quizapp.dto.CreateQuizRequest;
import com.example.quizapp.dto.QuizDTO;
import com.example.quizapp.entity.Quiz;
import com.example.quizapp.exception.ResourceNotFoundException;
import com.example.quizapp.repository.QuizRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Quiz CRUD operations.
 */
@RestController
@RequestMapping("/api/v1/quizzes")
@Tag(name = "Quiz", description = "Quiz management API")
public class QuizController {

    private final QuizRepository quizRepository;

    public QuizController(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    /**
     * GET /api/v1/quizzes - Get all quizzes with pagination
     */
    @GetMapping
    @Operation(summary = "Get all quizzes", description = "Returns a paginated list of quizzes")
    public ResponseEntity<Page<QuizDTO>> getAllQuizzes(Pageable pageable) {
        Page<QuizDTO> quizzes = quizRepository.findAll(pageable).map(QuizDTO::new);
        return ResponseEntity.ok(quizzes);
    }

    /**
     * GET /api/v1/quizzes/{id} - Get quiz by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get quiz by ID", description = "Returns a single quiz by its ID")
    public ResponseEntity<QuizDTO> getQuizById(@PathVariable Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", id));
        return ResponseEntity.ok(new QuizDTO(quiz));
    }

    /**
     * GET /api/v1/quizzes/search - Search quizzes by title
     */
    @GetMapping("/search")
    @Operation(summary = "Search quizzes", description = "Search quizzes by title")
    public ResponseEntity<List<QuizDTO>> searchQuizzes(@RequestParam String title) {
        List<QuizDTO> quizzes = quizRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(QuizDTO::new)
                .toList();
        return ResponseEntity.ok(quizzes);
    }

    /**
     * POST /api/v1/quizzes - Create a new quiz
     */
    @PostMapping
    @Operation(summary = "Create quiz", description = "Creates a new quiz")
    public ResponseEntity<QuizDTO> createQuiz(@RequestBody CreateQuizRequest request) {
        Quiz quiz = new Quiz();
        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setTimeLimit(request.getTimeLimit());
        quiz.setShuffleQuestions(request.getShuffleQuestions());
        quiz.setShuffleAnswers(request.getShuffleAnswers());
        quiz.setNegativePoints(request.getNegativePoints());
        
        Quiz savedQuiz = quizRepository.save(quiz);
        return ResponseEntity.status(HttpStatus.CREATED).body(new QuizDTO(savedQuiz));
    }

    /**
     * PUT /api/v1/quizzes/{id} - Update a quiz
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update quiz", description = "Updates an existing quiz")
    public ResponseEntity<QuizDTO> updateQuiz(@PathVariable Long id, @RequestBody CreateQuizRequest request) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", id));
        
        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setTimeLimit(request.getTimeLimit());
        quiz.setShuffleQuestions(request.getShuffleQuestions());
        quiz.setShuffleAnswers(request.getShuffleAnswers());
        quiz.setNegativePoints(request.getNegativePoints());
        
        Quiz updatedQuiz = quizRepository.save(quiz);
        return ResponseEntity.ok(new QuizDTO(updatedQuiz));
    }

    /**
     * DELETE /api/v1/quizzes/{id} - Delete a quiz
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete quiz", description = "Deletes a quiz by ID")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        if (!quizRepository.existsById(id)) {
            throw new ResourceNotFoundException("Quiz", id);
        }
        quizRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
