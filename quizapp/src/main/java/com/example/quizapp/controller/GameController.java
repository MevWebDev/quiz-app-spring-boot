package com.example.quizapp.controller;

import com.example.quizapp.dto.QuizDTO;
import com.example.quizapp.dto.QuizResultDTO;
import com.example.quizapp.entity.*;
import com.example.quizapp.exception.ResourceNotFoundException;
import com.example.quizapp.repository.*;
import com.example.quizapp.service.QuizService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for the quiz game flow.
 * Handles: home page, playing quizzes, submitting answers, and ranking.
 */
@Controller
public class GameController {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final QuizResultRepository quizResultRepository;
    private final QuizService quizService;

    public GameController(QuizRepository quizRepository,
                         QuestionRepository questionRepository,
                         AnswerRepository answerRepository,
                         QuizResultRepository quizResultRepository,
                         QuizService quizService) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.quizResultRepository = quizResultRepository;
        this.quizService = quizService;
    }

    /**
     * Home page - list of available quizzes
     */
    @GetMapping("/")
    public String home(Model model) {
        // Use service which handles transactions properly
        model.addAttribute("quizzes", quizService.getAllQuizzes(PageRequest.of(0, 20)).getContent());
        return "game/home";
    }


    /**
     * Start quiz - enter nickname
     */
    @GetMapping("/play/{quizId}")
    public String startQuiz(@PathVariable Long quizId, Model model) {
        QuizDTO quiz = quizService.getQuizById(quizId);
        model.addAttribute("quiz", quiz);
        return "game/start";
    }

    /**
     * Begin playing - save nickname and show questions
     */
    @PostMapping("/play/{quizId}")
    public String playQuiz(@PathVariable Long quizId,
                          @RequestParam String nickname,
                          HttpSession session,
                          Model model) {
        QuizDTO quiz = quizService.getQuizById(quizId);
        
        // Store game data in session
        session.setAttribute("quizId", quizId);
        session.setAttribute("nickname", nickname);
        session.setAttribute("startTime", System.currentTimeMillis());
        
        // Get questions (shuffle if enabled)
        List<Question> questions = questionRepository.findByQuizId(quizId);
        if (quiz.getShuffleQuestions() != null && quiz.getShuffleQuestions()) {
            Collections.shuffle(questions);
        }
        
        // Prepare questions with answers
        List<Map<String, Object>> questionData = new ArrayList<>();
        for (Question q : questions) {
            Map<String, Object> qData = new HashMap<>();
            qData.put("id", q.getId());
            qData.put("text", q.getText());
            qData.put("type", q.getType().name());
            qData.put("points", q.getPoints());
            
            List<Answer> answers = answerRepository.findByQuestionIdOrderByOrderIndexAsc(q.getId());
            if (quiz.getShuffleAnswers() != null && quiz.getShuffleAnswers()) {
                Collections.shuffle(answers);
            }
            qData.put("answers", answers);
            questionData.add(qData);
        }
        
        model.addAttribute("quiz", quiz);
        model.addAttribute("questions", questionData);
        model.addAttribute("nickname", nickname);
        model.addAttribute("timeLimit", quiz.getTimeLimit());
        
        return "game/play";
    }

    /**
     * Submit quiz answers and calculate score
     */
    @PostMapping("/submit/{quizId}")
    public String submitQuiz(@PathVariable Long quizId,
                            @RequestParam Map<String, String> answers,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        // Use service to get quiz data safely
        QuizDTO quizDTO = quizService.getQuizById(quizId);
        
        String nickname = (String) session.getAttribute("nickname");
        Long startTime = (Long) session.getAttribute("startTime");
        
        if (nickname == null) {
            nickname = "Anonymous";
        }
        
        // Calculate time taken
        int timeTaken = 0;
        if (startTime != null) {
            timeTaken = (int) ((System.currentTimeMillis() - startTime) / 1000);
        }
        
        // Check time limit
        if (quizDTO.getTimeLimit() != null && timeTaken > quizDTO.getTimeLimit()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Time limit exceeded!");
        }
        
        // Calculate score
        List<Question> questions = questionRepository.findByQuizId(quizId);
        int totalScore = 0;
        int maxScore = 0;
        
        for (Question question : questions) {
            maxScore += question.getPoints();
            String answerKey = "answer_" + question.getId();
            String userAnswer = answers.get(answerKey);
            
            System.out.println("DEBUG: Question " + question.getId() + " (" + question.getText() + ")");
            System.out.println("DEBUG: User answer: " + userAnswer);
            
            if (userAnswer != null && !userAnswer.isEmpty()) {
                boolean correct = checkAnswer(question, userAnswer);
                System.out.println("DEBUG: Correct? " + correct);
                if (correct) {
                    totalScore += question.getPoints();
                } else if (quizDTO.getNegativePoints() != null && quizDTO.getNegativePoints()) {
                    totalScore -= 1; // Deduct 1 point for wrong answer
                }
            } else {
                System.out.println("DEBUG: No answer provided");
            }
        }
        
        // Ensure score doesn't go negative
        totalScore = Math.max(0, totalScore);
        
        // Save result - need quiz entity for relationship
        Quiz quizEntity = quizRepository.findById(quizId).orElse(null);
        if (quizEntity != null) {
            QuizResult result = new QuizResult();
            result.setNickname(nickname);
            result.setScore(totalScore);
            result.setMaxScore(maxScore);
            result.setQuiz(quizEntity);
            result.setCompletedAt(LocalDateTime.now());
            quizResultRepository.save(result);
        }
        
        // Clear session
        session.removeAttribute("quizId");
        session.removeAttribute("nickname");
        session.removeAttribute("startTime");
        
        redirectAttributes.addFlashAttribute("score", totalScore);
        redirectAttributes.addFlashAttribute("maxScore", maxScore);
        redirectAttributes.addFlashAttribute("nickname", nickname);
        
        return "redirect:/result/" + quizId;
    }

    /**
     * Show quiz result
     */
    @GetMapping("/result/{quizId}")
    public String showResult(@PathVariable Long quizId, Model model) {
        QuizDTO quiz = quizService.getQuizById(quizId);
        
        List<QuizResult> ranking = quizResultRepository.findTop10ByQuizIdOrderByScoreDesc(quizId);
        
        model.addAttribute("quiz", quiz);
        model.addAttribute("ranking", ranking.stream().map(QuizResultDTO::new).toList());
        
        return "game/result";
    }

    /**
     * Show ranking for a quiz
     */
    @GetMapping("/ranking/{quizId}")
    public String showRanking(@PathVariable Long quizId, Model model) {
        QuizDTO quiz = quizService.getQuizById(quizId);
        
        List<QuizResult> ranking = quizResultRepository.findByQuizIdOrderByScoreDesc(quizId);
        
        model.addAttribute("quiz", quiz);
        model.addAttribute("ranking", ranking.stream().map(QuizResultDTO::new).toList());
        
        return "game/ranking";
    }

    /**
     * Global ranking page
     */
    @GetMapping("/ranking")
    public String globalRanking(Model model) {
        model.addAttribute("quizzes", quizService.getAllQuizzes(PageRequest.of(0, 100)).getContent());
        return "game/global-ranking";
    }

    /**
     * Check if user's answer is correct
     */
    private boolean checkAnswer(Question question, String userAnswer) {
        List<Answer> correctAnswers = answerRepository.findByQuestionIdAndIsCorrectTrue(question.getId());
        
        System.out.println("DEBUG checkAnswer: Question type=" + question.getType());
        System.out.println("DEBUG checkAnswer: User answer ID=" + userAnswer);
        System.out.println("DEBUG checkAnswer: Correct answer IDs=" + correctAnswers.stream().map(a -> a.getId().toString()).toList());
        
        switch (question.getType()) {
            case SINGLE_CHOICE:
            case TRUE_FALSE:
            case DROPDOWN:
                // Check if selected answer ID matches correct answer
                try {
                    Long answerId = Long.parseLong(userAnswer);
                    boolean match = correctAnswers.stream().anyMatch(a -> a.getId().equals(answerId));
                    System.out.println("DEBUG checkAnswer: Match=" + match);
                    return match;
                } catch (NumberFormatException e) {
                    return false;
                }
            
            case MULTIPLE_CHOICE:
                // User selects multiple answers (comma-separated IDs)
                String[] selectedIds = userAnswer.split(",");
                Set<Long> selectedSet = Arrays.stream(selectedIds)
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(Long::parseLong)
                        .collect(Collectors.toSet());
                Set<Long> correctSet = correctAnswers.stream()
                        .map(Answer::getId)
                        .collect(Collectors.toSet());
                return selectedSet.equals(correctSet);
            
            case SHORT_ANSWER:
            case FILL_BLANK:
                // Compare text (case-insensitive)
                return correctAnswers.stream()
                        .anyMatch(a -> a.getText().equalsIgnoreCase(userAnswer.trim()));
            
            case SORTING:
            case MATCHING:
                // For sorting/matching, check if order matches expected
                String[] parts = userAnswer.split(",");
                List<Long> correctOrder = correctAnswers.stream()
                        .sorted(Comparator.comparingInt(a -> a.getOrderIndex() != null ? a.getOrderIndex() : 0))
                        .map(Answer::getId)
                        .toList();
                try {
                    List<Long> userOrder = Arrays.stream(parts)
                            .map(String::trim)
                            .map(Long::parseLong)
                            .toList();
                    return userOrder.equals(correctOrder);
                } catch (NumberFormatException e) {
                    return false;
                }
            
            default:
                return false;
        }
    }
}
