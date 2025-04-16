package com.example.pfa_uplaod.controller;

import com.example.pfa_uplaod.modal.FileMetaData;
import com.example.pfa_uplaod.service.AnalysisService;
import com.example.pfa_uplaod.service.FileAnalysisService;
import com.example.pfa_uplaod.service.OpenAIService;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.time.LocalDateTime;
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

            // Analyze file to extract metrics (e.g., rows, missing values etc.)
            Map<String, Object> analysisResult = fileAnalysisService.analyzeFile(file);

            // Extract full text from the file for further analysis
            String fileText = extractTextFromFile(file);

            // Perform RGPD conformity check using OpenAIService
            Map<String, Object> conformityResult = openAIService.checkConformity(fileText);
            analysisResult.put("rgpd_analysis", conformityResult);

            // Optionally, call detailed AI analysis (currently commented out)
            // String aiAnalysis = openAIService.analyzeDocument(fileText);
            // analysisResult.put("analyse_openai", aiAnalysis);

            // Prepare FileMetaData entity
            FileMetaData metadata = new FileMetaData();
            metadata.setFileName(file.getOriginalFilename());
            metadata.setFileType(determineFileType(file));
            metadata.setFileSize(file.getSize());
            metadata.setUploadDate(LocalDateTime.now());

            // Persist analysis results along with file metadata
            analysisService.saveAnalysisResults(metadata, analysisResult);

            return ResponseEntity.ok(analysisResult);

        } catch (Exception e) {
            logger.error("Error analyzing file: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Erreur lors de l'analyse du fichier: " + e.getMessage()));
        }
    }

    /**
     * Determine file type based on extension or content type.
     */
    private String determineFileType(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName != null && fileName.contains(".")) {
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
            // Handle common Excel file types
            if (extension.equals("XLSX") || extension.equals("XLS")) {
                return extension;
            }
        }

        // Fallback: use content type if available
        String contentType = file.getContentType();
        if (contentType == null) {
            return "AUTRE";
        }
        String[] typeParts = contentType.split("/");
        return typeParts.length > 1 ? typeParts[1].toUpperCase() : "AUTRE";
    }

    /**
     * Extracts text from the uploaded file using Apache Tika.
     */
    private String extractTextFromFile(MultipartFile file) {
        try {
            BodyContentHandler handler = new BodyContentHandler(-1);
            AutoDetectParser parser = new AutoDetectParser();
            org.apache.tika.metadata.Metadata metadata = new org.apache.tika.metadata.Metadata(); // ✅ ligne ajoutée

            parser.parse(file.getInputStream(), handler, metadata); // ✅ passe metadata ici

            return handler.toString();
        } catch (IOException | TikaException | SAXException e) {
            logger.error("Error extracting text from file: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur extraction texte: " + e.getMessage());
        }
    }
}