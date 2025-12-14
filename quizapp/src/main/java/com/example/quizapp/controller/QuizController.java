package com.example.quizapp.controller;

import com.example.quizapp.dto.CreateQuizRequest;
import com.example.quizapp.dto.QuizDTO;
import com.example.quizapp.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Quiz CRUD operations.
 * Uses service layer and @Valid for validation.
 */
@RestController
@RequestMapping("/api/v1/quizzes")
@Tag(name = "Quiz", description = "Quiz management API")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    /**
     * GET /api/v1/quizzes - Get all quizzes with pagination
     */
    @GetMapping
    @Operation(summary = "Get all quizzes", description = "Returns a paginated list of quizzes")
    public ResponseEntity<Page<QuizDTO>> getAllQuizzes(Pageable pageable) {
        return ResponseEntity.ok(quizService.getAllQuizzes(pageable));
    }

    /**
     * GET /api/v1/quizzes/{id} - Get quiz by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get quiz by ID", description = "Returns a single quiz by its ID")
    public ResponseEntity<QuizDTO> getQuizById(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getQuizById(id));
    }

    /**
     * GET /api/v1/quizzes/search - Search quizzes by title
     */
    @GetMapping("/search")
    @Operation(summary = "Search quizzes", description = "Search quizzes by title")
    public ResponseEntity<List<QuizDTO>> searchQuizzes(@RequestParam String title) {
        return ResponseEntity.ok(quizService.searchQuizzes(title));
    }

    /**
     * POST /api/v1/quizzes - Create a new quiz
     */
    @PostMapping
    @Operation(summary = "Create quiz", description = "Creates a new quiz")
    public ResponseEntity<QuizDTO> createQuiz(@Valid @RequestBody CreateQuizRequest request) {
        QuizDTO created = quizService.createQuiz(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PUT /api/v1/quizzes/{id} - Update a quiz
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update quiz", description = "Updates an existing quiz")
    public ResponseEntity<QuizDTO> updateQuiz(@PathVariable Long id, @Valid @RequestBody CreateQuizRequest request) {
        return ResponseEntity.ok(quizService.updateQuiz(id, request));
    }

    /**
     * DELETE /api/v1/quizzes/{id} - Delete a quiz
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete quiz", description = "Deletes a quiz by ID")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }
}
