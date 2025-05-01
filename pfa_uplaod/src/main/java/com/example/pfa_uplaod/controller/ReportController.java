package com.example.pfa_uplaod.controller;

import com.example.pfa_uplaod.modal.FileAnalysis;
import com.example.pfa_uplaod.modal.FileMetaData;
import com.example.pfa_uplaod.modal.UserEntity;
import com.example.pfa_uplaod.repository.UserRepository;
import com.example.pfa_uplaod.service.AnalysisService;
import com.example.pfa_uplaod.service.FileAnalysisService;
import com.example.pfa_uplaod.service.OpenAIService;
import org.apache.tika.exception.TikaException;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    private final FileAnalysisService fileAnalysisService;
    private final AnalysisService analysisService;
    private final OpenAIService openAIService;
    private final UserRepository userRepository;

    @Autowired
    public ReportController(
            FileAnalysisService fileAnalysisService,
            AnalysisService analysisService,
            OpenAIService openAIService,
            UserRepository userRepository
    ) {
        this.fileAnalysisService = fileAnalysisService;
        this.analysisService = analysisService;
        this.openAIService = openAIService;
        this.userRepository = userRepository;
    }

    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeFile(
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            // 1. Get authenticated user
            String username = userDetails.getUsername();
            UserEntity user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));


            // 2. Validate file
            logger.info("Starting file analysis for: {}", file.getOriginalFilename());
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Aucun fichier sélectionné !"));
            }

            // 3. Analyze file
            Map<String, Object> analysisResult = fileAnalysisService.analyzeFile(file);
            String fileText = extractTextFromFile(file);

            // 4. Perform RGPD analysis
            Map<String, Object> conformityResult = openAIService.checkConformity(fileText);
            analysisResult.put("rgpd_analysis", conformityResult);

            // 5. Save metadata
            FileMetaData metadata = new FileMetaData();
            metadata.setFileName(file.getOriginalFilename());
            metadata.setFileType(determineFileType(file));
            metadata.setFileSize(file.getSize());
            metadata.setUploadDate(LocalDateTime.now());
            metadata.setUploadedBy(user);
            metadata.setOrganisation_name(username);

// Create and pass a new FileAnalysis object
            FileAnalysis fileAnalysis = new FileAnalysis();
            analysisService.saveAnalysisResults(metadata, analysisResult, fileAnalysis); // Add FileAnalysis parameter
            // 6. Return response (no authResponse needed)
            return ResponseEntity.ok(analysisResult);

        } catch (Exception e) {
            logger.error("Error analyzing file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
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