package com.example.pfa_uplaod.service;

import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FileAnalysisService {
    private static final Logger logger = LoggerFactory.getLogger(FileAnalysisService.class);
    private static final String DEFAULT_FILE_TYPE = "UNKNOWN";

    public Map<String, Object> analyzeFile(MultipartFile file) {
        Map<String, Object> result = new HashMap<>();

        try {
            String fileName = file.getOriginalFilename();
            logger.info("Analyzing file: {}", fileName);

            if (fileName == null) {
                logger.error("Invalid file name");
                throw new IllegalArgumentException("Nom de fichier invalide");
            }

            String fileType = getFileExtension(fileName);
            logger.info("File type detected: {}", fileType);

            Map<String, Object> analysisResult;

            switch (fileType.toLowerCase()) {
                case "csv":
                    logger.info("Processing CSV file");
                    analysisResult = analyzeCSV(file);
                    break;
                case "json":
                    logger.info("Processing JSON file");
                    analysisResult = analyzeJSON(file);
                    break;
                case "xlsx":
                case "xls":
                    logger.info("Processing Excel file");
                    analysisResult = analyzeExcel(file);
                    break;
                default:
                    logger.error("Unsupported file type: {}", fileType);
                    throw new IllegalArgumentException("Format non supporté: " + fileType);
            }

            // Copie toutes les clés (y compris "columns")
            result.putAll(analysisResult);
            logger.info("Analysis completed successfully");
            return result;

        } catch (IOException e) {
            logger.error("Error analyzing file: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur d'analyse du fichier", e);
        } catch (Exception e) {
            logger.error("Unexpected error during file analysis: {}", e.getMessage(), e);
            result.put("error", e.getMessage());
            return result;
        }
    }

    private Map<String, Object> analyzeCSV(MultipartFile file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            List<String> lines = reader.lines().collect(Collectors.toList());
            int totalRows = lines.size();
            int missingValues = 0;
            int columns = 0;

            // Détermine le nombre maximal de colonnes
            for (String line : lines) {
                int currentColumns = line.split(",", -1).length;
                if (currentColumns > columns)
                    columns = currentColumns;
            }

            // Compte les valeurs manquantes
            for (String line : lines) {
                String[] values = line.split(",", -1);
                for (String value : values) {
                    if (value.trim().isEmpty())
                        missingValues++;
                }
            }

            return createResult(
                    "CSV",
                    totalRows,
                    missingValues,
                    calculateMissingPercentage(totalRows, columns, missingValues),
                    columns // <-- Colonnes passées explicitement
            );
        }
    }

    private Map<String, Object> analyzeJSON(MultipartFile file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(file.getInputStream());

        int totalRows = 0;
        int missingValues = 0;
        int columns = 0;

        if (rootNode.isArray()) {
            totalRows = rootNode.size();
            for (JsonNode node : rootNode) {
                if (totalRows == 1)
                    columns = node.size();
                missingValues += countMissingValues(node);
            }
        }

        return createResult(
                "JSON",
                totalRows,
                missingValues,
                calculateMissingPercentage(totalRows, columns, missingValues),
                columns // <-- Colonnes passées explicitement
        );
    }

    private Map<String, Object> analyzeExcel(MultipartFile file) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            int totalRows = sheet.getPhysicalNumberOfRows();
            int missingValues = 0;
            int columns = 0;

            if (totalRows > 0) {
                Row firstRow = sheet.getRow(0);
                if (firstRow != null)
                    columns = firstRow.getPhysicalNumberOfCells();

                for (Row row : sheet) {
                    for (int i = 0; i < columns; i++) {
                        Cell cell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        if (cell == null || cell.getCellType() == CellType.BLANK) {
                            missingValues++;
                        }
                    }
                }
            }

            return createResult(
                    "Excel",
                    totalRows,
                    missingValues,
                    calculateMissingPercentage(totalRows, columns, missingValues),
                    columns // <-- Colonnes passées explicitement
            );
        }
    }

    private int countMissingValues(JsonNode node) {
        int missing = 0;
        for (JsonNode value : node) {
            if (value.isNull() || (value.isValueNode() && value.asText().trim().isEmpty())) {
                missing++;
            }
        }
        return missing;
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1)
            return DEFAULT_FILE_TYPE;
        return fileName.substring(lastDotIndex + 1);
    }

    private double calculateMissingPercentage(int totalRows, int columns, int missingValues) {
        if (totalRows == 0 || columns == 0)
            return 0.0;
        return (missingValues * 100.0) / (totalRows * columns);
    }

    private Map<String, Object> createResult(String type, int rows, int missing, double percentage, int columns) {
        Map<String, Object> result = new HashMap<>();
        result.put("file_type", type);
        result.put("total_rows", rows);
        result.put("missing_values", missing);
        result.put("missing_percentage", percentage);
        result.put("columns", columns); // <-- Colonnes ajoutées
        return result;
    }
}