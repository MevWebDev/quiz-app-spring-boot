package com.example.quizapp.controller;

import com.example.quizapp.dto.AnswerDTO;
import com.example.quizapp.dto.CreateQuizRequest;
import com.example.quizapp.dto.QuestionDTO;
import com.example.quizapp.dto.QuizDTO;
import com.example.quizapp.entity.*;
import com.example.quizapp.exception.ResourceNotFoundException;
import com.example.quizapp.repository.*;
import com.example.quizapp.service.QuestionService;
import com.example.quizapp.service.QuizService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Admin Controller for managing quizzes, questions and answers.
 */
@Controller
@RequestMapping("/admin")
@Transactional(readOnly = true)
public class AdminController {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final QuizService quizService;
    private final QuestionService questionService;

    public AdminController(QuizRepository quizRepository,
                          QuestionRepository questionRepository,
                          AnswerRepository answerRepository,
                          QuizService quizService,
                          QuestionService questionService) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.quizService = quizService;
        this.questionService = questionService;
    }

    /**
     * Admin dashboard
     */
    @GetMapping
    public String dashboard(Model model) {
        // Use service to avoid lazy loading issues
        model.addAttribute("quizzes", quizService.getAllQuizzes(PageRequest.of(0, 100)).getContent());
        model.addAttribute("totalQuizzes", quizRepository.count());
        model.addAttribute("totalQuestions", questionRepository.count());
        model.addAttribute("totalAnswers", answerRepository.count());
        return "admin/dashboard";
    }

    /**
     * Show form to create new quiz
     */
    @GetMapping("/quiz/new")
    public String newQuizForm(Model model) {
        model.addAttribute("quiz", new CreateQuizRequest());
        model.addAttribute("isEdit", false);
        return "admin/quiz-form";
    }

    /**
     * Create a new quiz
     */
    @PostMapping("/quiz")
    @Transactional
    public String createQuiz(@ModelAttribute CreateQuizRequest request,
                            RedirectAttributes redirectAttributes) {
        QuizDTO created = quizService.createQuiz(request);
        redirectAttributes.addFlashAttribute("successMessage", "Quiz created successfully!");
        return "redirect:/admin/quiz/" + created.getId() + "/questions";
    }

    /**
     * Show form to edit quiz
     */
    @GetMapping("/quiz/{id}/edit")
    public String editQuizForm(@PathVariable Long id, Model model) {
        QuizDTO quiz = quizService.getQuizById(id);
        CreateQuizRequest request = new CreateQuizRequest();
        request.setTitle(quiz.getTitle());
        request.setDescription(quiz.getDescription());
        request.setTimeLimit(quiz.getTimeLimit());
        request.setShuffleQuestions(quiz.getShuffleQuestions());
        request.setShuffleAnswers(quiz.getShuffleAnswers());
        request.setNegativePoints(quiz.getNegativePoints());
        
        model.addAttribute("quiz", request);
        model.addAttribute("quizId", id);
        model.addAttribute("isEdit", true);
        return "admin/quiz-form";
    }

    /**
     * Update quiz
     */
    @PostMapping("/quiz/{id}")
    @Transactional
    public String updateQuiz(@PathVariable Long id,
                            @ModelAttribute CreateQuizRequest request,
                            RedirectAttributes redirectAttributes) {
        quizService.updateQuiz(id, request);
        redirectAttributes.addFlashAttribute("successMessage", "Quiz updated successfully!");
        return "redirect:/admin";
    }

    /**
     * Delete quiz
     */
    @PostMapping("/quiz/{id}/delete")
    @Transactional
    public String deleteQuiz(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        quizService.deleteQuiz(id);
        redirectAttributes.addFlashAttribute("successMessage", "Quiz deleted successfully!");
        return "redirect:/admin";
    }

    /**
     * Manage questions for a quiz
     */
    @GetMapping("/quiz/{quizId}/questions")
    public String manageQuestions(@PathVariable Long quizId, Model model) {
        QuizDTO quiz = quizService.getQuizById(quizId);
        List<QuestionDTO> questions = questionService.getQuestionsByQuiz(quizId, true);
        
        model.addAttribute("quiz", quiz);
        model.addAttribute("questions", questions);
        model.addAttribute("questionTypes", QuestionType.values());
        return "admin/questions";
    }

    /**
     * Add a new question
     */
    @PostMapping("/quiz/{quizId}/questions")
    @Transactional
    public String addQuestion(@PathVariable Long quizId,
                             @RequestParam String text,
                             @RequestParam QuestionType type,
                             @RequestParam(defaultValue = "1") Integer points,
                             RedirectAttributes redirectAttributes) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", quizId));
        
        Question question = new Question();
        question.setText(text);
        question.setType(type);
        question.setPoints(points);
        question.setQuiz(quiz);
        question.setOrderIndex(questionRepository.findByQuizId(quizId).size() + 1);
        
        questionRepository.save(question);
        redirectAttributes.addFlashAttribute("successMessage", "Question added successfully!");
        return "redirect:/admin/quiz/" + quizId + "/questions";
    }

    /**
     * Delete a question
     */
    @PostMapping("/questions/{id}/delete")
    @Transactional
    public String deleteQuestion(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", id));
        Long quizId = question.getQuiz().getId();
        
        questionRepository.delete(question);
        redirectAttributes.addFlashAttribute("successMessage", "Question deleted!");
        return "redirect:/admin/quiz/" + quizId + "/questions";
    }

    /**
     * Update an existing question
     */
    @PostMapping("/questions/{id}/update")
    @Transactional
    public String updateQuestion(@PathVariable Long id,
                                @RequestParam String text,
                                @RequestParam QuestionType type,
                                @RequestParam(defaultValue = "1") Integer points,
                                RedirectAttributes redirectAttributes) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", id));
        Long quizId = question.getQuiz().getId();
        
        question.setText(text);
        question.setType(type);
        question.setPoints(points);
        
        questionRepository.save(question);
        redirectAttributes.addFlashAttribute("successMessage", "Question updated successfully!");
        return "redirect:/admin/quiz/" + quizId + "/questions";
    }

    /**
     * Manage answers for a question
     */
    @GetMapping("/questions/{questionId}/answers")
    public String manageAnswers(@PathVariable Long questionId, Model model) {
        QuestionDTO question = questionService.getQuestionById(questionId);
        List<Answer> answers = answerRepository.findByQuestionIdOrderByOrderIndexAsc(questionId);
        
        model.addAttribute("question", question);
        model.addAttribute("answers", answers.stream().map(AnswerDTO::new).toList());
        model.addAttribute("quizId", question.getQuizId());
        return "admin/answers";
    }

    /**
     * Add a new answer
     */
    @PostMapping("/questions/{questionId}/answers")
    @Transactional
    public String addAnswer(@PathVariable Long questionId,
                           @RequestParam String text,
                           @RequestParam(defaultValue = "false") Boolean isCorrect,
                           @RequestParam(required = false) Integer orderIndex,
                           RedirectAttributes redirectAttributes) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", questionId));
        
        Answer answer = new Answer();
        answer.setText(text);
        answer.setIsCorrect(isCorrect);
        answer.setOrderIndex(orderIndex != null ? orderIndex : answerRepository.findByQuestionId(questionId).size() + 1);
        answer.setQuestion(question);
        
        answerRepository.save(answer);
        redirectAttributes.addFlashAttribute("successMessage", "Answer added successfully!");
        return "redirect:/admin/questions/" + questionId + "/answers";
    }

    /**
     * Delete an answer
     */
    @PostMapping("/answers/{id}/delete")
    @Transactional
    public String deleteAnswer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Answer", id));
        Long questionId = answer.getQuestion().getId();
        
        answerRepository.delete(answer);
        redirectAttributes.addFlashAttribute("successMessage", "Answer deleted!");
        return "redirect:/admin/questions/" + questionId + "/answers";
    }
}
