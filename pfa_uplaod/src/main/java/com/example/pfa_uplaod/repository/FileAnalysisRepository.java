package com.example.pfa_uplaod.repository;

import com.example.pfa_uplaod.modal.FileAnalysis;
import com.example.pfa_uplaod.modal.FileMetaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Map;
import java.util.Optional;

public interface FileAnalysisRepository extends JpaRepository<FileAnalysis, Long> {

    @Query("SELECT " +
            "SUM(fa.totalRows * metadata.columns) as totalCells, " +
            "SUM(fa.missingValues) as totalMissing " +
            "FROM FileAnalysis fa " +
            "LEFT JOIN fa.metadata metadata " +
            "WHERE metadata.columns IS NOT NULL") // <-- Filtre les NULL
    Map<String, Object> getGlobalMissingStats();

    @Query("SELECT a FROM FileAnalysis a ORDER BY a.analysisDate DESC")
    Page<FileAnalysis> findAllWithPagination(Pageable pageable);

    Optional<FileAnalysis> findTopByMetadataOrderByAnalysisDateDesc(FileMetaData metadata);

    @Query("SELECT AVG(f.llmConformityScore) FROM FileAnalysis f")
    Double getAvgLlmConformityScore();
}