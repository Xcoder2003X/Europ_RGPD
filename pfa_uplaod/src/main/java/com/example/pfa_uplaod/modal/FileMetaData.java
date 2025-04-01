package com.example.pfa_uplaod.modal;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "file_metadata")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileMetaData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String fileType;
    private Long fileSize;
    private LocalDateTime uploadDate;

    @Column(nullable = false) // <-- Colonne non-nullable
    private Integer columns = 0; // Valeur par défaut

    @OneToOne(mappedBy = "metadata", cascade = CascadeType.ALL, orphanRemoval = true)
    private FileAnalysis analysis;
}