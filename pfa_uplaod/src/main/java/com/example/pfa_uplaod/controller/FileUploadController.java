package com.example.pfa_uplaod.controller;

import com.example.pfa_uplaod.modal.FileMetaData;
import com.example.pfa_uplaod.service.AnalysisService;
import com.example.pfa_uplaod.service.FileAnalysisService;
import com.example.pfa_uplaod.service.OpenAIService;
import com.example.pfa_uplaod.service.ReportService;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {
    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);
    private static final String REPORTS_DIR = "reports";

    private final FileAnalysisService fileAnalysisService;
    private final ReportService reportService;
    private final AnalysisService analysisService;
    private final OpenAIService openAIService;

    @Autowired
    public FileUploadController(FileAnalysisService fileAnalysisService,
                                ReportService reportService,
                                AnalysisService analysisService,
                                OpenAIService openAIService) {
        this.fileAnalysisService = fileAnalysisService;
        this.reportService = reportService;
        this.analysisService = analysisService;
        this.openAIService = openAIService;

        createReportsDirectory();
    }

    private void createReportsDirectory() {
        File reportsDir = new File(REPORTS_DIR);
        try {
            if (!reportsDir.exists()) {
                boolean created = reportsDir.mkdirs();
                if (created) {
                    logger.info("Reports directory created successfully at: {}", reportsDir.getAbsolutePath());
                } else {
                    logger.error("Failed to create reports directory at: {}", reportsDir.getAbsolutePath());
                }
            } else {
                logger.info("Reports directory already exists at: {}", reportsDir.getAbsolutePath());
            }
        } catch (Exception e) {
            logger.error("Error creating reports directory: {}", e.getMessage(), e);
        }
    }

    @PostMapping("/upload")
    @Transactional
    public ResponseEntity<?> uploadFile(@RequestPart("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Aucun fichier sélectionné !"));
            }

            Map<String, Object> analysisResult = fileAnalysisService.analyzeFile(file);

            FileMetaData metadata = new FileMetaData();
            metadata.setFileName(file.getOriginalFilename());
            metadata.setFileType(determineFileType(file));
            metadata.setFileSize(file.getSize());
            metadata.setUploadDate(LocalDateTime.now());

            analysisService.saveAnalysisResults(metadata, analysisResult);

            return ResponseEntity.ok(analysisResult);
        } catch (Exception e) {
            logger.error("Error uploading file: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/generate-report")
    public ResponseEntity<?> generateReport(@RequestParam("file") MultipartFile file) {
        try {
            logger.info("Starting report generation for file: {}", file.getOriginalFilename());

            if (file.isEmpty()) {
                logger.warn("Empty file received");
                return ResponseEntity.badRequest().body(Map.of("error", "Aucun fichier sélectionné !"));
            }

            String timestamp = LocalDateTime.now().toString().replace(":", "-");
            String fileName = file.getOriginalFilename().replaceFirst("[.][^.]+$", "");
            String reportFileName = String.format("%s_%s.pdf", fileName, timestamp);
            String filePath = REPORTS_DIR + File.separator + reportFileName;
            logger.info("Generated report path: {}", filePath);

            Map<String, Object> analysisResult = fileAnalysisService.analyzeFile(file);
            logger.info("File analysis completed");

            String fileText = extractTextFromFile(file);
            logger.info("Text extraction completed, length: {}", fileText.length());

            String aiAnalysis = openAIService.analyzeDocument(fileText);
            logger.info("AI analysis completed");

            analysisResult.put("analyse_openai", aiAnalysis);

            reportService.generatePdfReport(analysisResult, filePath);
            logger.info("PDF generation completed");

            File reportFile = new File(filePath);
            if (!reportFile.exists()) {
                logger.error("Report file not found at: {}", filePath);
                throw new IOException("Le rapport n'a pas été généré correctement");
            }
            logger.info("Report file found, size: {} bytes", reportFile.length());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + reportFileName)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(new FileInputStream(filePath)));
        } catch (Exception e) {
            logger.error("Error generating report: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Erreur lors de la génération du rapport: " + e.getMessage()));
        }
    }

    private String determineFileType(MultipartFile file) throws IOException {
        String type = Files.probeContentType(file.getResource().getFile().toPath());
        return (type != null) ? type.split("/")[1].toUpperCase() : "AUTRE";
    }

    private String extractTextFromFile(MultipartFile file) {
        try {
            return new Tika().parseToString(file.getInputStream());
        } catch (IOException | TikaException e) {
            logger.error("Error extracting text from file: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur extraction texte", e);
        }
    }
}