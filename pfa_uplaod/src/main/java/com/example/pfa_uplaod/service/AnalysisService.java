package com.example.pfa_uplaod.service;

import com.example.pfa_uplaod.modal.FileAnalysis;
import com.example.pfa_uplaod.modal.FileMetaData;
import com.example.pfa_uplaod.modal.UserEntity;
import com.example.pfa_uplaod.repository.FileAnalysisRepository;
import com.example.pfa_uplaod.repository.FileMetadataRepository;
import com.example.pfa_uplaod.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.time.LocalDateTime;

@Service
@Transactional
public class AnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(AnalysisService.class);

    private final FileMetadataRepository metadataRepo;
    private final FileAnalysisRepository analysisRepo;
    private final UserRepository userRepository;

    @Autowired
    public AnalysisService(FileMetadataRepository metadataRepo,
            FileAnalysisRepository analysisRepo,
            UserRepository userRepository) {
        this.metadataRepo = metadataRepo;
        this.analysisRepo = analysisRepo;
        this.userRepository = userRepository;
    }

    public void saveAnalysisResults(FileMetaData metadata, Map<String, Object> analysisResult, FileAnalysis fileAnalysis) {
        try {
            // 1. Save metadata first
            metadata = metadataRepo.save(metadata);

            // 2. Set metadata reference in file analysis
            fileAnalysis.setMetadata(metadata);

            // 3. Convert and set values from analysisResult
            fileAnalysis.setTotalRows(safeConvertToInteger(analysisResult.get("total_rows")));
            fileAnalysis.setMissingValues(safeConvertToInteger(analysisResult.get("missing_values")));
            fileAnalysis.setMissingPercentage(safeConvertToDouble(analysisResult.get("missing_percentage")));

            // 4. Handle RGPD analysis
            Map<String, Object> rgpdAnalysis = (Map<String, Object>) analysisResult.get("rgpd_analysis");
            if (rgpdAnalysis != null) {
                fileAnalysis.setLlmConformityScore(safeConvertToInteger(rgpdAnalysis.get("score_conformite")));
                fileAnalysis.setHasConsent(safeConvertToBoolean(rgpdAnalysis.get("consentement_valide")));
            }

            // 5. Calculate final score
            fileAnalysis.setConformityScore(calculateConformityScore(
                    fileAnalysis.getMissingPercentage(),
                    fileAnalysis.getHasConsent()
            ));

            // 6. Set analysis date
            fileAnalysis.setAnalysisDate(LocalDateTime.now());

            // 7. Save the analysis
            analysisRepo.save(fileAnalysis);

        } catch (Exception e) {
            logger.error("Persist error: {}", e.getMessage(), e);
            throw new RuntimeException("Persistence failed: " + e.getMessage(), e);
        }
    }

    private Integer safeConvertToInteger(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (Exception e) {
            return 0;
        }
    }

    private Double safeConvertToDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return 0.0;
        }
    }

    private Boolean safeConvertToBoolean(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        try {
            return Boolean.parseBoolean(value.toString());
        } catch (Exception e) {
            return false;
        }
    }

    private Double calculateConformityScore(Double missingPercentage, Boolean hasConsent) {
        double score = 100.0;
        if (missingPercentage != null) {
            score -= missingPercentage * 0.5;
        }
        if (hasConsent != null && !hasConsent) {
            score -= 20.0;
        }
        return Math.max(0.0, Math.min(100.0, score));
    }
}
