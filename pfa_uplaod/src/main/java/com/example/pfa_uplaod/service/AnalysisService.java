package com.example.pfa_uplaod.service;

import com.example.pfa_uplaod.modal.FileAnalysis;
import com.example.pfa_uplaod.modal.FileMetaData;
import com.example.pfa_uplaod.repository.FileAnalysisRepository;
import com.example.pfa_uplaod.repository.FileMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional
public class AnalysisService {

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
            // Validation des données
            validateAnalysisData(analysisResult);

            // Conversion et sauvegarde de metadata
            Integer columns = (Integer) analysisResult.get("columns");
            metadata.setColumns(columns != null ? columns : 0); // Valeur par défaut
            metadataRepo.save(metadata); // <-- Sauvegarde metadata en premier

            // Création de l'analyse
            analysis.setMetadata(metadata);
            analysis.setTotalRows(safeConvertToInteger(analysisResult.get("total_rows")));
            analysis.setMissingValues(safeConvertToInteger(analysisResult.get("missing_values")));
            analysis.setMissingPercentage(safeConvertToDouble(analysisResult.get("missing_percentage")));
            analysis.setConformityScore(calculateConformityScore(
                    analysis.getMissingPercentage(),
                    safeConvertToBoolean(analysisResult.get("consentement_valide"))
            ));

            // Sauvegarde de l'analyse
            analysisRepo.save(analysis);

        } catch (Exception e) {
            throw new RuntimeException("Échec de la persistance: " + e.getMessage(), e);
        }
    }

    private void validateAnalysisData(Map<String, Object> data) {
        if (!data.containsKey("total_rows")) {
            throw new IllegalArgumentException("Données d'analyse incomplètes: total_rows manquant");
        }
        if (!data.containsKey("missing_values")) {
            throw new IllegalArgumentException("Données d'analyse incomplètes: missing_values manquant");
        }
    }

    private Integer safeConvertToInteger(Object value) {
        if (value instanceof Number) return ((Number) value).intValue();
        return 0;
    }

    private Double safeConvertToDouble(Object value) {
        if (value instanceof Number) return ((Number) value).doubleValue();
        return 0.0;
    }

    private Boolean safeConvertToBoolean(Object value) {
        if (value instanceof Boolean) return (Boolean) value;
        return false;
    }

    private Double calculateConformityScore(Double missingPercentage, Boolean hasConsent) {
        double score = 100.0;
        if (missingPercentage != null) score -= missingPercentage * 0.5;
        if (hasConsent != null && !hasConsent) score -= 20.0;
        return Math.max(0.0, Math.min(100.0, score));
    }
}