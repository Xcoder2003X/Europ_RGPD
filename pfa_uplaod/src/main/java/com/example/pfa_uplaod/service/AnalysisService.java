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

    public void saveAnalysisResults(FileMetaData metadata, Map<String, Object> analysisResult) {
        FileAnalysis analysis = new FileAnalysis();

        try {


            // 2) Lier et persister le metadata

            Integer columns = safeConvertToInteger(analysisResult.get("columns"));
            metadata.setColumns(columns != null ? columns : 0);
            FileMetaData savedMeta = metadataRepo.save(metadata);

            // 3) Extraction du score LLM
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
            analysis.setLlmConformityScore(llmScore);

            // 4) Mapper le reste des champs
            analysis.setMetadata(savedMeta);
            analysis.setTotalRows(safeConvertToInteger(analysisResult.get("total_rows")));
            analysis.setMissingValues(safeConvertToInteger(analysisResult.get("missing_values")));
            analysis.setMissingPercentage(safeConvertToDouble(analysisResult.get("missing_percentage")));
            analysis.setConformityScore(
                    calculateConformityScore(analysis.getMissingPercentage(),
                            safeConvertToBoolean(analysisResult.get("consentement_valide")))
            );

            // 5) Sauvegarde de l'analyse
            analysisRepo.save(analysis);


        } catch (Exception e) {
            logger.error("Persist error: {}", e.getMessage(), e);
            throw new RuntimeException("Échec de la persistance: " + e.getMessage(), e);
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

