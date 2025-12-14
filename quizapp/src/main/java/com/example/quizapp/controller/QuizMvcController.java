package com.example.quizapp.controller;

import com.example.quizapp.dto.CreateQuizRequest;
import com.example.quizapp.dto.QuizDTO;
import com.example.quizapp.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * MVC Controller for Thymeleaf views.
 * Demonstrates @Controller, Model, @ModelAttribute, th:each, th:object, th:errors.
 */
@Controller
@RequestMapping("/quizzes")
public class QuizMvcController {

    private final QuizService quizService;

    public QuizMvcController(QuizService quizService) {
        this.quizService = quizService;
    }

    /**
     * List all quizzes with pagination
     */
    @GetMapping
    public String listQuizzes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Page<QuizDTO> quizPage = quizService.getAllQuizzes(PageRequest.of(page, size));
        model.addAttribute("quizzes", quizPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", quizPage.getTotalPages());
        model.addAttribute("totalItems", quizPage.getTotalElements());
        return "quiz/list";
    }

    /**
     * Show quiz details
     */
    @GetMapping("/{id}")
    public String viewQuiz(@PathVariable Long id, Model model) {
        QuizDTO quiz = quizService.getQuizById(id);
        model.addAttribute("quiz", quiz);
        return "quiz/view";
    }

    /**
     * Show form to create new quiz
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("quiz", new CreateQuizRequest());
        return "quiz/form";
    }

    /**
     * Handle quiz creation with validation
     */
    @PostMapping
    public String createQuiz(
            @Valid @ModelAttribute("quiz") CreateQuizRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            return "quiz/form";
        }
        
        QuizDTO created = quizService.createQuiz(request);
        redirectAttributes.addFlashAttribute("successMessage", "Quiz created successfully!");
        return "redirect:/quizzes/" + created.getId();
    }

    /**
     * Show form to edit quiz
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
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
        return "quiz/form";
    }

    /**
     * Handle quiz update
     */
    @PostMapping("/{id}")
    public String updateQuiz(
            @PathVariable Long id,
            @Valid @ModelAttribute("quiz") CreateQuizRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("quizId", id);
            model.addAttribute("isEdit", true);
            return "quiz/form";
        }
        
        quizService.updateQuiz(id, request);
        redirectAttributes.addFlashAttribute("successMessage", "Quiz updated successfully!");
        return "redirect:/quizzes/" + id;
    }

    /**
     * Handle quiz deletion
     */
    @PostMapping("/{id}/delete")
    public String deleteQuiz(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        quizService.deleteQuiz(id);
        redirectAttributes.addFlashAttribute("successMessage", "Quiz deleted successfully!");
        return "redirect:/quizzes";
    }
}
