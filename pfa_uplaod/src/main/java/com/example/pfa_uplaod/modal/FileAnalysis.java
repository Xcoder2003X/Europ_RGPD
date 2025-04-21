package com.example.pfa_uplaod.modal;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "file_analysis")
public class FileAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY ,cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "metadata_id")
    private FileMetaData metadata;

    @Column(name = "total_rows")
    private Integer totalRows;

    @Column(name = "missing_values")
    private Integer missingValues;

    @Column(name = "missing_percentage", columnDefinition = "DECIMAL(5,2)")
    private Double missingPercentage;

    @Column(name = "has_consent")
    private Boolean hasConsent;

    @Column(name = "conformity_score", columnDefinition = "DECIMAL(5,2)")
    private Double conformityScore;

    @Column(name = "ai_analysis", columnDefinition = "TEXT")
    private String aiAnalysis;

    @Column(name = "analysis_date")
    private LocalDateTime analysisDate;




    // Constructeurs
    public FileAnalysis() {
        this.analysisDate = LocalDateTime.now();
    }

    public FileAnalysis(FileMetaData metadata, Map<String, Object> analysisResult) {
        this.metadata = metadata;
        this.totalRows = (Integer) analysisResult.get("total_rows");
        this.missingValues = (Integer) analysisResult.getOrDefault("missing_values", 0);
        this.missingPercentage = (Double) analysisResult.getOrDefault("missing_percentage", 0.0);
        this.analysisDate = LocalDateTime.now();
        this.hasConsent = (Boolean) analysisResult.getOrDefault("consentement_valide", false);
        this.aiAnalysis = (String) analysisResult.get("analyse_openai");
    }

    // Getters/Setters
    public Long getId() {
        return id;
    }

    public Boolean getHasConsent() {
        return hasConsent;
    }

    public Double getMissingPercentage(){
        return missingPercentage;
    }

    public FileMetaData getMetadata() {
        return metadata;
    }

    public void setMetadata(FileMetaData metadata) {
        this.metadata = metadata;
    }

    public Integer getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(Integer totalRows) {
        this.totalRows = totalRows;
    }

    public void setHasConsent(Boolean consentementValide) {
        this.hasConsent = consentementValide;
    }

    public void setAiAnalysis(String analyseOpenai) {
        this.aiAnalysis=analyseOpenai;
    }

    public void setConformityScore(Double aDouble) {
        this.conformityScore = aDouble;
    }

    public void setMissingValues(Integer missingValues) {
        this.missingValues = missingValues;
    }

    public void setMissingPercentage(Double missingPercentage) {
        this.missingPercentage = missingPercentage;
    }

    public Double getConformityScore() {
return conformityScore;    }

    // ✅ Correct LLM score definition
    @Column(name = "llm_conformity_score", nullable = false)
    private Integer llmConformityScore = 0;

    // Getters/setters
    public Integer getLlmConformityScore() {
        return llmConformityScore;
    }

    public void setLlmConformityScore(Integer llmConformityScore) {
        this.llmConformityScore = llmConformityScore != null ? llmConformityScore : 0;
    }



    // ... autres getters/setters
}
