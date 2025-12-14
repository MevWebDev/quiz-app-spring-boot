package com.example.quizapp.controller;

import com.example.quizapp.dto.AnswerDTO;
import com.example.quizapp.entity.Answer;
import com.example.quizapp.entity.Question;
import com.example.quizapp.exception.ResourceNotFoundException;
import com.example.quizapp.repository.AnswerRepository;
import com.example.quizapp.repository.QuestionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Answer CRUD operations.
 */
@RestController
@RequestMapping("/api/v1/answers")
@Tag(name = "Answer", description = "Answer management API")
public class AnswerController {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    public AnswerController(AnswerRepository answerRepository, QuestionRepository questionRepository) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
    }

    /**
     * GET /api/v1/answers - Get all answers with pagination
     */
    @GetMapping
    @Operation(summary = "Get all answers")
    public ResponseEntity<Page<AnswerDTO>> getAllAnswers(Pageable pageable) {
        Page<AnswerDTO> answers = answerRepository.findAll(pageable).map(AnswerDTO::new);
        return ResponseEntity.ok(answers);
    }

    /**
     * GET /api/v1/answers/{id} - Get answer by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get answer by ID")
    public ResponseEntity<AnswerDTO> getAnswerById(@PathVariable Long id) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Answer", id));
        return ResponseEntity.ok(new AnswerDTO(answer));
    }

    /**
     * GET /api/v1/answers/question/{questionId} - Get answers by question
     */
    @GetMapping("/question/{questionId}")
    @Operation(summary = "Get answers by question ID")
    public ResponseEntity<List<AnswerDTO>> getAnswersByQuestion(@PathVariable Long questionId) {
        List<AnswerDTO> answers = answerRepository.findByQuestionIdOrderByOrderIndexAsc(questionId)
                .stream()
                .map(AnswerDTO::new)
                .toList();
        return ResponseEntity.ok(answers);
    }

    /**
     * POST /api/v1/answers - Create a new answer
     */
    @PostMapping
    @Operation(summary = "Create answer")
    public ResponseEntity<AnswerDTO> createAnswer(@RequestBody AnswerDTO request) {
        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Question", request.getQuestionId()));
        
        Answer answer = new Answer();
        answer.setText(request.getText());
        answer.setIsCorrect(request.getIsCorrect() != null ? request.getIsCorrect() : false);
        answer.setOrderIndex(request.getOrderIndex());
        answer.setQuestion(question);
        
        Answer saved = answerRepository.save(answer);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AnswerDTO(saved));
    }

    /**
     * PUT /api/v1/answers/{id} - Update an answer
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update answer")
    public ResponseEntity<AnswerDTO> updateAnswer(@PathVariable Long id, @RequestBody AnswerDTO request) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Answer", id));
        
        answer.setText(request.getText());
        answer.setIsCorrect(request.getIsCorrect());
        answer.setOrderIndex(request.getOrderIndex());
        
        Answer updated = answerRepository.save(answer);
        return ResponseEntity.ok(new AnswerDTO(updated));
    }

    /**
     * DELETE /api/v1/answers/{id} - Delete an answer
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete answer")
    public ResponseEntity<Void> deleteAnswer(@PathVariable Long id) {
        if (!answerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Answer", id);
        }
        answerRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
