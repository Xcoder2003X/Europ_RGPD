package com.example.pfa_uplaod.service;

import com.example.pfa_uplaod.modal.FileAnalysis;
import com.example.pfa_uplaod.modal.FileMetaData;
import com.example.pfa_uplaod.repository.FileAnalysisRepository;
import com.example.pfa_uplaod.repository.FileMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

@Service
@Transactional
public class AnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(AnalysisService.class);

    private final FileMetadataRepository metadataRepo;
    private final FileAnalysisRepository analysisRepo;

    @Autowired
    public AnalysisService(FileMetadataRepository metadataRepo, FileAnalysisRepository analysisRepo) {
        this.metadataRepo = metadataRepo;
        this.analysisRepo = analysisRepo;
    }

    public void saveAnalysisResults(FileMetaData metadata, Map<String, Object> analysisResult) {
        FileAnalysis analysis = new FileAnalysis();

        try {
            validateAnalysisData(analysisResult); // Updated validation

            // Metadata handling (unchanged)
            Integer columns = safeConvertToInteger(analysisResult.get("columns"));
            metadata.setColumns(columns != null ? columns : 0);
            metadataRepo.save(metadata);

            // ✅ Improved LLM score extraction
            Map<String, Object> rgpdAnalysis = (Map<String, Object>) analysisResult.get("rgpd_analysis");
            Integer llmScore = 0;

            if (rgpdAnalysis != null) {
                Object scoreObj = rgpdAnalysis.get("score_conformite");
                if (scoreObj instanceof Number) {
                    llmScore = ((Number) scoreObj).intValue();
                } else if (scoreObj != null) {
                    try {
                        llmScore = Integer.parseInt(scoreObj.toString());
                    } catch (NumberFormatException e) {
                        logger.warn("Invalid score format: {}", scoreObj);
                    }
                }
            }

            // ✅ Set LLM score explicitly
            analysis.setLlmConformityScore(llmScore);

            // Other fields (unchanged)
            analysis.setMetadata(metadata);
            analysis.setTotalRows(safeConvertToInteger(analysisResult.get("total_rows")));
            analysis.setMissingValues(safeConvertToInteger(analysisResult.get("missing_values")));
            analysis.setMissingPercentage(safeConvertToDouble(analysisResult.get("missing_percentage")));
            analysis.setConformityScore(calculateConformityScore(
                    analysis.getMissingPercentage(),
                    safeConvertToBoolean(analysisResult.get("consentement_valide"))
            ));

            analysisRepo.save(analysis);
            logger.info("Saved analysis for: {}", metadata.getFileName());

        } catch (Exception e) {
            logger.error("Persist error: {}", e.getMessage());
            throw new RuntimeException("Échec de la persistance: " + e.getMessage(), e);
        }
    }

    // ✅ Added RGPD analysis check
    private void validateAnalysisData(Map<String, Object> data) {
        if (!data.containsKey("total_rows")) {
            throw new IllegalArgumentException("Données manquantes: total_rows");
        }
        if (!data.containsKey("rgpd_analysis")) {
            throw new IllegalArgumentException("Données manquantes: rgpd_analysis");
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
