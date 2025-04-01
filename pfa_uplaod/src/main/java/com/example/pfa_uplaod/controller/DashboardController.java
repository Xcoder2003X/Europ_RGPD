package com.example.pfa_uplaod.controller;

import com.example.pfa_uplaod.modal.FileAnalysis;
import com.example.pfa_uplaod.modal.FileMetaData;
import com.example.pfa_uplaod.repository.FileAnalysisRepository;
import com.example.pfa_uplaod.repository.FileMetadataRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
public class DashboardController {

        private final FileMetadataRepository metadataRepository;
        private final FileAnalysisRepository analysisRepository;

        public DashboardController(FileMetadataRepository metadataRepository,
                        FileAnalysisRepository analysisRepository) {
                this.metadataRepository = metadataRepository;
                this.analysisRepository = analysisRepository;
        }

        @GetMapping("/dashboard")
        public ResponseEntity<Map<String, Object>> getDashboardData(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "8") int size) {
                Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "analysisDate"));
                Page<FileAnalysis> analysisPage = analysisRepository.findAllWithPagination(pageable);
                Map<String, Object> globalStats = analysisRepository.getGlobalMissingStats();

                Long totalCells = (Long) globalStats.getOrDefault("totalCells", 0L);
                Long totalMissing = (Long) globalStats.getOrDefault("totalMissing", 0L);
                double globalMissingPercentage = totalCells > 0 ? (totalMissing * 100.0) / totalCells : 0.0;

                Map<String, Object> dashboardData = new HashMap<>();
                try {
                        // Données agrégées
                        dashboardData.put("totalFiles", metadataRepository.count());
                        dashboardData.put("avgMissingPercentage", globalMissingPercentage);

                        // Distribution des types de fichiers
                        List<Map<String, Object>> fileTypes = metadataRepository.getFileTypeDistribution()
                                        .stream()
                                        .map(entry -> Map.of(
                                                        "type", entry.getOrDefault("type", "INCONNU"),
                                                        "count", entry.getOrDefault("count", 0L)))
                                        .collect(Collectors.toList());
                        dashboardData.put("fileTypes", fileTypes);

                        // Scores de conformité paginés
                        List<Map<String, Object>> conformityScores = analysisPage.getContent()
                                        .stream()
                                        .map(analysis -> {
                                                Map<String, Object> map = new HashMap<>();
                                                map.put("name", getFileName(analysis));
                                                map.put("type", analysis.getMetadata().getFileType() != null
                                                                ? analysis.getMetadata().getFileType()
                                                                : "INCONNU");
                                                map.put("score", analysis.getConformityScore() != null
                                                                ? analysis.getConformityScore()
                                                                : 0.0);
                                                return map;
                                        })
                                        .collect(Collectors.toList());
                        dashboardData.put("conformityScores", conformityScores);

                        // Fichiers récents
                        List<Map<String, Object>> recentFiles = metadataRepository.findTop5ByOrderByUploadDateDesc()
                                        .stream()
                                        .map(metadata -> new HashMap<String, Object>(Map.of(
                                                        "name",
                                                        metadata.getFileName() != null ? metadata.getFileName()
                                                                        : "Sans nom",
                                                        "type",
                                                        metadata.getFileType() != null ? metadata.getFileType()
                                                                        : "INCONNU",
                                                        "score", getLatestScoreForFile(metadata))))
                                        .collect(Collectors.toList());

                        dashboardData.put("recentFiles", recentFiles);

                        // Métadonnées de pagination
                        dashboardData.put("currentPage", analysisPage.getNumber());
                        dashboardData.put("totalPages", analysisPage.getTotalPages());
                        dashboardData.put("totalItems", analysisPage.getTotalElements());

                        return ResponseEntity.ok(dashboardData);

                } catch (Exception e) {
                        Map<String, Object> error = new HashMap<>();
                        error.put("error", "Erreur de traitement: " + e.getMessage());
                        return ResponseEntity.internalServerError().body(error);
                }
        }

        private String getFileName(FileAnalysis analysis) {
                return analysis.getMetadata() != null && analysis.getMetadata().getFileName() != null
                                ? analysis.getMetadata().getFileName()
                                : "Fichier inconnu";
        }

        private Double getLatestScoreForFile(FileMetaData metadata) {
                return analysisRepository.findTopByMetadataOrderByAnalysisDateDesc(metadata)
                                .map(fa -> fa.getConformityScore() != null ? fa.getConformityScore() : 0.0)
                                .orElse(0.0);
        }
}