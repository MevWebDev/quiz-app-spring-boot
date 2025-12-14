package com.example.quizapp.controller;

import com.example.quizapp.dto.QuizResultDTO;
import com.example.quizapp.entity.Quiz;
import com.example.quizapp.entity.QuizResult;
import com.example.quizapp.exception.ResourceNotFoundException;
import com.example.quizapp.repository.QuizRepository;
import com.example.quizapp.repository.QuizResultRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for QuizResult (ranking) operations.
 */
@RestController
@RequestMapping("/api/v1/results")
@Tag(name = "Quiz Results", description = "Quiz results and ranking API")
public class QuizResultController {

    private final QuizResultRepository resultRepository;
    private final QuizRepository quizRepository;

    public QuizResultController(QuizResultRepository resultRepository, QuizRepository quizRepository) {
        this.resultRepository = resultRepository;
        this.quizRepository = quizRepository;
    }

    /**
     * GET /api/v1/results - Get all results with pagination
     */
    @GetMapping
    @Operation(summary = "Get all results")
    public ResponseEntity<Page<QuizResultDTO>> getAllResults(Pageable pageable) {
        Page<QuizResultDTO> results = resultRepository.findAll(pageable).map(QuizResultDTO::new);
        return ResponseEntity.ok(results);
    }

    /**
     * GET /api/v1/results/{id} - Get result by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get result by ID")
    public ResponseEntity<QuizResultDTO> getResultById(@PathVariable Long id) {
        QuizResult result = resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QuizResult", id));
        return ResponseEntity.ok(new QuizResultDTO(result));
    }

    /**
     * GET /api/v1/results/quiz/{quizId}/ranking - Get ranking for a quiz
     */
    @GetMapping("/quiz/{quizId}/ranking")
    @Operation(summary = "Get top 10 ranking for a quiz")
    public ResponseEntity<List<QuizResultDTO>> getRankingByQuiz(@PathVariable Long quizId) {
        List<QuizResultDTO> ranking = resultRepository.findTop10ByQuizIdOrderByScoreDesc(quizId)
                .stream()
                .map(QuizResultDTO::new)
                .toList();
        return ResponseEntity.ok(ranking);
    }

    /**
     * GET /api/v1/results/quiz/{quizId} - Get all results for a quiz with pagination
     */
    @GetMapping("/quiz/{quizId}")
    @Operation(summary = "Get all results for a quiz")
    public ResponseEntity<Page<QuizResultDTO>> getResultsByQuiz(@PathVariable Long quizId, Pageable pageable) {
        Page<QuizResultDTO> results = resultRepository.findByQuizId(quizId, pageable).map(QuizResultDTO::new);
        return ResponseEntity.ok(results);
    }

    /**
     * POST /api/v1/results - Submit a quiz result
     */
    @PostMapping
    @Operation(summary = "Submit quiz result")
    public ResponseEntity<QuizResultDTO> submitResult(@RequestBody QuizResultDTO request) {
        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", request.getQuizId()));
        
        QuizResult result = new QuizResult();
        result.setNickname(request.getNickname());
        result.setScore(request.getScore());
        result.setMaxScore(request.getMaxScore());
        result.setQuiz(quiz);
        
        QuizResult saved = resultRepository.save(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(new QuizResultDTO(saved));
    }

    /**
     * DELETE /api/v1/results/{id} - Delete a result
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete result")
    public ResponseEntity<Void> deleteResult(@PathVariable Long id) {
        if (!resultRepository.existsById(id)) {
            throw new ResourceNotFoundException("QuizResult", id);
        }
        resultRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
