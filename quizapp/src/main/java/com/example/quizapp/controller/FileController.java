package com.example.quizapp.controller;

import com.example.quizapp.dto.QuizDTO;
import com.example.quizapp.service.FileService;
import com.example.quizapp.service.QuizService;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for file upload, download, and export operations.
 */
@Controller
@RequestMapping("/files")
public class FileController {

    private final FileService fileService;
    private final QuizService quizService;

    public FileController(FileService fileService, QuizService quizService) {
        this.fileService = fileService;
        this.quizService = quizService;
    }

    /**
     * Upload a file using MultipartFile
     */
    @PostMapping("/upload")
    public String uploadFile(
            @RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) {
        try {
            String filename = fileService.uploadFile(file);
            redirectAttributes.addFlashAttribute("successMessage", "File uploaded: " + filename);
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Upload failed: " + e.getMessage());
        }
        return "redirect:/quizzes";
    }

    /**
     * Download a file as Resource
     */
    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        Resource resource = fileService.downloadFile(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    /**
     * Export quizzes to CSV - ResponseEntity<byte[]>
     */
    @GetMapping("/export/quizzes/csv")
    public ResponseEntity<byte[]> exportQuizzesToCsv() {
        Page<QuizDTO> quizzes = quizService.getAllQuizzes(PageRequest.of(0, 1000));
        
        String[] headers = {"ID", "Title", "Description", "Time Limit", "Questions"};
        List<String[]> data = new ArrayList<>();
        
        for (QuizDTO quiz : quizzes.getContent()) {
            data.add(new String[]{
                quiz.getId().toString(),
                quiz.getTitle(),
                quiz.getDescription() != null ? quiz.getDescription() : "",
                quiz.getTimeLimit() != null ? quiz.getTimeLimit().toString() : "",
                quiz.getQuestionCount().toString()
            });
        }
        
        byte[] csvBytes = fileService.exportToCsv(data, headers);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"quizzes.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvBytes);
    }
}
