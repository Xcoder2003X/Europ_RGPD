package com.example.pfa_uplaod.service;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.io.font.constants.StandardFonts;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class ReportService {
    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    // Configuration des couleurs
    private static final Color HEADER_COLOR = ColorConstants.BLUE;
    private static final Color TEXT_COLOR = ColorConstants.DARK_GRAY;
    private static final Color BACKGROUND_COLOR = ColorConstants.LIGHT_GRAY;

    public void generatePdfReport(Map<String, Object> analysisReport, String filePath) throws IOException {
        try (PdfWriter writer = new PdfWriter(filePath);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc)) {

            // Create fonts per document
            PdfFont headerFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont bodyFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            addHeader(document, headerFont);
            addMetadataSection(document, analysisReport, headerFont, bodyFont);
            addAnalysisSection(document, analysisReport, headerFont, bodyFont);
            addFooter(document, bodyFont);

        } catch (Exception e) {
            logger.error("Error generating PDF report: {}", e.getMessage(), e);
            throw new IOException("Failed to generate PDF report: " + e.getMessage(), e);
        }
    }

    // Helper methods with font parameters
    private void addHeader(Document document, PdfFont headerFont) {
        Div header = new Div()
                .setBackgroundColor(HEADER_COLOR)
                .setPadding(20)
                .setTextAlignment(TextAlignment.CENTER);

        Text title = new Text("Rapport d'Analyse des Données")
                .setFontSize(24)
                .setFont(headerFont)
                .setFontColor(ColorConstants.WHITE);

        header.add(new Paragraph(title));
        document.add(header);
    }

    private void addMetadataSection(Document document,
                                    Map<String, Object> analysisReport,
                                    PdfFont headerFont,
                                    PdfFont bodyFont) {
        Div metadataSection = new Div()
                .setPadding(20)
                .setBackgroundColor(BACKGROUND_COLOR);

        Text metadataTitle = new Text("Informations Générales")
                .setFontSize(18)
                .setFont(headerFont)
                .setFontColor(HEADER_COLOR);

        metadataSection.add(new Paragraph(metadataTitle));

        Table metadataTable = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();

        addTableRow(metadataTable, "Total de lignes", analysisReport.get("total_rows"), headerFont, bodyFont);
        addTableRow(metadataTable, "Valeurs manquantes", analysisReport.get("missing_values"), headerFont, bodyFont);
        addTableRow(metadataTable, "Pourcentage de valeurs manquantes",
                analysisReport.get("missing_percentage"), headerFont, bodyFont);

        metadataSection.add(metadataTable);
        document.add(metadataSection);
    }

    private void addAnalysisSection(Document document,
                                    Map<String, Object> analysisReport,
                                    PdfFont headerFont,
                                    PdfFont bodyFont) {
        Div analysisSection = new Div().setPadding(20);

        Text analysisTitle = new Text("Analyse Détaillée")
                .setFontSize(18)
                .setFont(headerFont)
                .setFontColor(HEADER_COLOR);

        analysisSection.add(new Paragraph(analysisTitle));

        String analysisText = (String) analysisReport.get("analyse_openai");
        if (analysisText != null && !analysisText.isBlank()) {
            formatAnalysisText(analysisSection, analysisText, headerFont, bodyFont);
        } else {
            analysisSection.add(new Paragraph("Aucune analyse disponible")
                    .setFont(bodyFont)
                    .setFontColor(TEXT_COLOR));
        }

        document.add(analysisSection);
    }

    private void formatAnalysisText(Div analysisSection,
                                    String analysisText,
                                    PdfFont headerFont,
                                    PdfFont bodyFont) {
        String[] sections = analysisText.split("\n");

        for (String section : sections) {
            if (section.isBlank()) continue;

            if (section.startsWith("-")) {
                analysisSection.add(createBulletPoint(section.substring(1), bodyFont));
            } else if (section.contains(":")) {
                String[] parts = section.split(":", 2);
                analysisSection.add(createTitleContent(parts[0], parts[1], headerFont, bodyFont));
            } else {
                analysisSection.add(createRegularParagraph(section, bodyFont));
            }
        }
    }

    private Paragraph createBulletPoint(String text, PdfFont bodyFont) {
        return new Paragraph("• " + text.trim())
                .setFont(bodyFont)
                .setFontColor(TEXT_COLOR)
                .setMarginLeft(20);
    }

    private Paragraph createTitleContent(String title,
                                         String content,
                                         PdfFont headerFont,
                                         PdfFont bodyFont) {
        Text titlePart = new Text(title + ":")
                .setFont(headerFont)
                .setFontColor(HEADER_COLOR);

        Text contentPart = new Text(" " + content.trim())
                .setFont(bodyFont)
                .setFontColor(TEXT_COLOR);

        return new Paragraph().add(titlePart).add(contentPart);
    }

    private Paragraph createRegularParagraph(String text, PdfFont bodyFont) {
        return new Paragraph(text.trim())
                .setFont(bodyFont)
                .setFontColor(TEXT_COLOR);
    }

    private void addFooter(Document document, PdfFont bodyFont) {
        Div footer = new Div()
                .setBackgroundColor(BACKGROUND_COLOR)
                .setPadding(10)
                .setTextAlignment(TextAlignment.CENTER);

        Text footerText = new Text("Rapport généré le " + LocalDateTime.now())
                .setFontSize(10)
                .setFont(bodyFont)
                .setFontColor(TEXT_COLOR);

        footer.add(new Paragraph(footerText));
        document.add(footer);
    }

    private void addTableRow(Table table,
                             String label,
                             Object value,
                             PdfFont headerFont,
                             PdfFont bodyFont) {
        String formattedValue = value != null ? value.toString() : "N/A";

        table.addCell(new Cell().add(new Paragraph(label)
                .setFont(headerFont)
                .setFontColor(HEADER_COLOR)));

        table.addCell(new Cell().add(new Paragraph(formattedValue)
                .setFont(bodyFont)
                .setFontColor(TEXT_COLOR)));
    }
}