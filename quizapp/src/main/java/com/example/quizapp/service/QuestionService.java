package com.example.quizapp.service;

import com.example.quizapp.dto.QuestionDTO;
import com.example.quizapp.entity.Question;
import com.example.quizapp.entity.Quiz;
import com.example.quizapp.entity.QuestionType;
import com.example.quizapp.exception.ResourceNotFoundException;
import com.example.quizapp.repository.QuestionRepository;
import com.example.quizapp.repository.QuizRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer for Question operations.
 */
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;

    public QuestionService(QuestionRepository questionRepository, QuizRepository quizRepository) {
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;
    }

    @Transactional(readOnly = true)
    public Page<QuestionDTO> getAllQuestions(Pageable pageable) {
        return questionRepository.findAll(pageable).map(QuestionDTO::new);
    }

    @Transactional(readOnly = true)
    public QuestionDTO getQuestionById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", id));
        return new QuestionDTO(question);
    }

    @Transactional(readOnly = true)
    public List<QuestionDTO> getQuestionsByQuiz(Long quizId, boolean ordered) {
        List<Question> questions = ordered 
            ? questionRepository.findByQuizIdOrderByOrderIndexAsc(quizId)
            : questionRepository.findByQuizId(quizId);
        return questions.stream().map(QuestionDTO::new).toList();
    }

    @Transactional(readOnly = false)
    public QuestionDTO createQuestion(QuestionDTO request) {
        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", request.getQuizId()));
        
        Question question = new Question();
        question.setText(request.getText());
        question.setType(request.getType() != null ? request.getType() : QuestionType.SINGLE_CHOICE);
        question.setPoints(request.getPoints() != null ? request.getPoints() : 1);
        question.setOrderIndex(request.getOrderIndex());
        question.setQuiz(quiz);
        
        Question saved = questionRepository.save(question);
        return new QuestionDTO(saved);
    }

    @Transactional(readOnly = false)
    public QuestionDTO updateQuestion(Long id, QuestionDTO request) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", id));
        
        question.setText(request.getText());
        question.setType(request.getType());
        question.setPoints(request.getPoints());
        question.setOrderIndex(request.getOrderIndex());
        
        Question updated = questionRepository.save(question);
        return new QuestionDTO(updated);
    }

    @Transactional(readOnly = false)
    public void deleteQuestion(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Question", id);
        }
        questionRepository.deleteById(id);
    }
}
