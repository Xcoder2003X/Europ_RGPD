package com.example.pfa_uplaod.repository;

import com.example.pfa_uplaod.modal.FileMetaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

// FileMetadataRepository.java
public interface FileMetadataRepository extends JpaRepository<FileMetaData, Long> {

    @Query("SELECT NEW map(COALESCE(fm.fileType, 'INCONNU') as type, COUNT(fm) as count) FROM FileMetaData fm GROUP BY COALESCE(fm.fileType, 'INCONNU')")
    List<Map<String, Object>> getFileTypeDistribution();

    List<FileMetaData> findTop5ByOrderByUploadDateDesc();
}

// FileAnalysisRepository.java
