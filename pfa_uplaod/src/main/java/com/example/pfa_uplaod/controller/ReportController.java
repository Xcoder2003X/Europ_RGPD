package com.example.pfa_uplaod.controller;

import com.example.pfa_uplaod.modal.FileMetaData;
import com.example.pfa_uplaod.service.AnalysisService;
import com.example.pfa_uplaod.service.FileAnalysisService;
import com.example.pfa_uplaod.service.OpenAIService;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    private final FileAnalysisService fileAnalysisService;
    private final AnalysisService analysisService;
    private final OpenAIService openAIService;

    @Autowired
    public ReportController(FileAnalysisService fileAnalysisService,
            AnalysisService analysisService,
            OpenAIService openAIService) {
        this.fileAnalysisService = fileAnalysisService;
        this.analysisService = analysisService;
        this.openAIService = openAIService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeFile(@RequestPart("file") MultipartFile file) {
        try {
            logger.info("Starting file analysis for: {}", file.getOriginalFilename());

            if (file.isEmpty()) {
                logger.warn("Empty file received");
                return ResponseEntity.badRequest().body(Map.of("error", "Aucun fichier sélectionné !"));
            }

            // Analyze the file structure and content
            Map<String, Object> analysisResult = fileAnalysisService.analyzeFile(file);
            logger.info("File analysis completed");

            // Extract text for RAG analysis
            String fileText = extractTextFromFile(file);
            logger.info("Text extraction completed, length: {}", fileText.length());

            // Get AI analysis with RAG context
            String aiAnalysis = openAIService.analyzeDocument(fileText);
            logger.info("AI analysis completed");

            // Add AI analysis to the results
            analysisResult.put("analyse_openai", aiAnalysis);

            // Save analysis results
            FileMetaData metadata = new FileMetaData();
            metadata.setFileName(file.getOriginalFilename());
            metadata.setFileType(determineFileType(file));
            metadata.setFileSize(file.getSize());
            metadata.setUploadDate(java.time.LocalDateTime.now());

            analysisService.saveAnalysisResults(metadata, analysisResult);

            return ResponseEntity.ok(analysisResult);

        } catch (Exception e) {
            logger.error("Error analyzing file: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Erreur lors de l'analyse du fichier: " + e.getMessage()));
        }
    }

    private String determineFileType(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName != null && fileName.contains(".")) {
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
            // Handle Excel file types
            if (extension.equals("XLSX") || extension.equals("XLS")) {
                return extension;
            }
        }

        // Fallback to content type
        String contentType = file.getContentType();
        if (contentType == null) {
            return "AUTRE";
        }
        return contentType.split("/")[1].toUpperCase();
    }

    private String extractTextFromFile(MultipartFile file) {
        try {
            return new Tika().parseToString(file.getInputStream());
        } catch (IOException | TikaException e) {
            logger.error("Error extracting text from file: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur extraction texte: " + e.getMessage());
        }
    }
}