package com.example.quizapp.controller;

import com.example.quizapp.dto.QuestionDTO;
import com.example.quizapp.entity.Question;
import com.example.quizapp.entity.Quiz;
import com.example.quizapp.entity.QuestionType;
import com.example.quizapp.exception.ResourceNotFoundException;
import com.example.quizapp.repository.QuestionRepository;
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
 * REST Controller for Question CRUD operations.
 */
@RestController
@RequestMapping("/api/v1/questions")
@Tag(name = "Question", description = "Question management API")
public class QuestionController {

    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;

    public QuestionController(QuestionRepository questionRepository, QuizRepository quizRepository) {
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;
    }

    /**
     * GET /api/v1/questions - Get all questions with pagination
     */
    @GetMapping
    @Operation(summary = "Get all questions", description = "Returns a paginated list of questions")
    public ResponseEntity<Page<QuestionDTO>> getAllQuestions(Pageable pageable) {
        Page<QuestionDTO> questions = questionRepository.findAll(pageable).map(QuestionDTO::new);
        return ResponseEntity.ok(questions);
    }

    /**
     * GET /api/v1/questions/{id} - Get question by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get question by ID")
    public ResponseEntity<QuestionDTO> getQuestionById(@PathVariable Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", id));
        return ResponseEntity.ok(new QuestionDTO(question));
    }

    /**
     * GET /api/v1/questions/quiz/{quizId} - Get questions by quiz
     */
    @GetMapping("/quiz/{quizId}")
    @Operation(summary = "Get questions by quiz ID")
    public ResponseEntity<List<QuestionDTO>> getQuestionsByQuiz(
            @PathVariable Long quizId,
            @RequestParam(defaultValue = "false") boolean ordered) {
        List<Question> questions = ordered 
            ? questionRepository.findByQuizIdOrderByOrderIndexAsc(quizId)
            : questionRepository.findByQuizId(quizId);
        return ResponseEntity.ok(questions.stream().map(QuestionDTO::new).toList());
    }

    /**
     * POST /api/v1/questions - Create a new question
     */
    @PostMapping
    @Operation(summary = "Create question")
    public ResponseEntity<QuestionDTO> createQuestion(@RequestBody QuestionDTO request) {
        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", request.getQuizId()));
        
        Question question = new Question();
        question.setText(request.getText());
        question.setType(request.getType() != null ? request.getType() : QuestionType.SINGLE_CHOICE);
        question.setPoints(request.getPoints() != null ? request.getPoints() : 1);
        question.setOrderIndex(request.getOrderIndex());
        question.setQuiz(quiz);
        
        Question saved = questionRepository.save(question);
        return ResponseEntity.status(HttpStatus.CREATED).body(new QuestionDTO(saved));
    }

    /**
     * PUT /api/v1/questions/{id} - Update a question
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update question")
    public ResponseEntity<QuestionDTO> updateQuestion(@PathVariable Long id, @RequestBody QuestionDTO request) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", id));
        
        question.setText(request.getText());
        question.setType(request.getType());
        question.setPoints(request.getPoints());
        question.setOrderIndex(request.getOrderIndex());
        
        Question updated = questionRepository.save(question);
        return ResponseEntity.ok(new QuestionDTO(updated));
    }

    /**
     * DELETE /api/v1/questions/{id} - Delete a question
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete question")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        if (!questionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Question", id);
        }
        questionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
