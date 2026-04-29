package com.trinova.scms.service;

import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// PDF imports (PDFBox 3.x)
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

// Word imports (Apache POI)
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.math.BigInteger;

/**
 * Generates Occupancy Report files in PDF, DOCX, and TXT formats.
 * <p>
 * Naming convention: SCMS_Occupancy_Report_YYYY-MM-DD.{ext}
 */
public class ReportExporter {

    private static final DateTimeFormatter DATE_FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIMESTAMP_FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Builds the base filename (without extension).
     */
    public static String baseFileName() {
        return "SCMS_Occupancy_Report_" + LocalDate.now().format(DATE_FMT);
    }

    // ─── TXT Export ────────────────────────────────────────────

    public static File exportTxt(DefaultTableModel model,
                                  File directory) throws IOException {
        File file = new File(directory, baseFileName() + ".txt");
        try (PrintWriter pw = new PrintWriter(
                new BufferedWriter(new FileWriter(file)))) {

            pw.println("═══════════════════════════════════════════════════════════════════════════");
            pw.println("              SMART COWORKING SPACE MANAGEMENT SYSTEM");
            pw.println("                       OCCUPANCY REPORT");
            pw.println("═══════════════════════════════════════════════════════════════════════════");
            pw.println("Generated: " +
                LocalDateTime.now().format(TIMESTAMP_FMT));
            pw.println();

            // Column headers
            int colCount = model.getColumnCount();
            int[] widths = {14, 18, 22, 22, 16, 20};
            StringBuilder header = new StringBuilder();
            for (int c = 0; c < colCount; c++) {
                header.append(
                    padRight(model.getColumnName(c), widths[c]));
            }
            pw.println(header);
            pw.println("─".repeat(header.length()));

            // Data rows
            int totalHotDesk = 0, totalMeeting = 0,
                totalPrivate = 0, totalAll = 0;
            double totalRevenue = 0;

            for (int r = 0; r < model.getRowCount(); r++) {
                StringBuilder row = new StringBuilder();
                for (int c = 0; c < colCount; c++) {
                    Object val = model.getValueAt(r, c);
                    row.append(padRight(
                        val != null ? val.toString() : "",
                        widths[c]));
                }
                pw.println(row);

                // Accumulate totals
                totalHotDesk  += toInt(model.getValueAt(r, 1));
                totalMeeting  += toInt(model.getValueAt(r, 2));
                totalPrivate  += toInt(model.getValueAt(r, 3));
                totalAll      += toInt(model.getValueAt(r, 4));
                totalRevenue  += toDouble(model.getValueAt(r, 5));
            }

            pw.println("─".repeat(header.length()));

            // Summary
            pw.println();
            pw.println("SUMMARY");
            pw.println("  Total Hot Desk Bookings     : " + totalHotDesk);
            pw.println("  Total Meeting Room Bookings : " + totalMeeting);
            pw.println("  Total Private Room Bookings : " + totalPrivate);
            pw.println("  Grand Total Bookings        : " + totalAll);
            pw.printf("  Grand Total Revenue         : PKR %.2f%n",
                totalRevenue);
            pw.println();
            pw.println("═══════════════════════════════════════════════════════════════════════════");
            pw.println("                         END OF REPORT");
            pw.println("═══════════════════════════════════════════════════════════════════════════");
        }
        return file;
    }

    // ─── PDF Export ────────────────────────────────────────────

    public static File exportPdf(DefaultTableModel model,
                                  File directory) throws IOException {
        File file = new File(directory, baseFileName() + ".pdf");

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            PDType1Font fontBold =
                new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font fontNormal =
                new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            float pageWidth = page.getMediaBox().getWidth();
            float margin = 40;
            float yStart = page.getMediaBox().getHeight() - 50;
            float y = yStart;

            PDPageContentStream cs =
                new PDPageContentStream(doc, page);

            // Title
            cs.beginText();
            cs.setFont(fontBold, 16);
            cs.newLineAtOffset(margin, y);
            cs.showText("SCMS — Occupancy Report");
            cs.endText();
            y -= 20;

            // Timestamp
            cs.beginText();
            cs.setFont(fontNormal, 9);
            cs.newLineAtOffset(margin, y);
            cs.showText("Generated: " +
                LocalDateTime.now().format(TIMESTAMP_FMT));
            cs.endText();
            y -= 25;

            // Table
            int colCount = model.getColumnCount();
            float tableWidth = pageWidth - 2 * margin;
            float[] colWidths = {70, 75, 95, 95, 75, 105};
            float rowHeight = 18;

            // Header row background
            cs.setNonStrokingColor(0.18f, 0.25f, 0.43f);
            cs.addRect(margin, y - rowHeight,
                tableWidth, rowHeight);
            cs.fill();

            // Header text
            cs.setNonStrokingColor(1f, 1f, 1f);
            cs.beginText();
            cs.setFont(fontBold, 8);
            float xPos = margin + 4;
            cs.newLineAtOffset(xPos, y - 13);
            for (int c = 0; c < colCount; c++) {
                if (c > 0) cs.newLineAtOffset(colWidths[c - 1], 0);
                cs.showText(model.getColumnName(c));
            }
            cs.endText();
            y -= rowHeight;

            // Data rows
            cs.setNonStrokingColor(0f, 0f, 0f);
            int totalHotDesk = 0, totalMeeting = 0,
                totalPrivate = 0, totalAll = 0;
            double totalRevenue = 0;

            for (int r = 0; r < model.getRowCount(); r++) {
                // Alternate row shading
                if (r % 2 == 0) {
                    cs.setNonStrokingColor(0.95f, 0.95f, 0.97f);
                    cs.addRect(margin, y - rowHeight,
                        tableWidth, rowHeight);
                    cs.fill();
                }

                cs.setNonStrokingColor(0.1f, 0.1f, 0.1f);
                cs.beginText();
                cs.setFont(fontNormal, 8);
                xPos = margin + 4;
                cs.newLineAtOffset(xPos, y - 13);
                for (int c = 0; c < colCount; c++) {
                    if (c > 0)
                        cs.newLineAtOffset(colWidths[c - 1], 0);
                    Object val = model.getValueAt(r, c);
                    cs.showText(val != null ?
                        val.toString() : "");
                }
                cs.endText();
                y -= rowHeight;

                totalHotDesk += toInt(model.getValueAt(r, 1));
                totalMeeting += toInt(model.getValueAt(r, 2));
                totalPrivate += toInt(model.getValueAt(r, 3));
                totalAll     += toInt(model.getValueAt(r, 4));
                totalRevenue += toDouble(model.getValueAt(r, 5));

                // New page if needed
                if (y < 100) {
                    cs.close();
                    PDPage newPage = new PDPage(PDRectangle.A4);
                    doc.addPage(newPage);
                    cs = new PDPageContentStream(doc, newPage);
                    y = newPage.getMediaBox().getHeight() - 50;
                }
            }

            // Summary section
            y -= 15;
            cs.beginText();
            cs.setFont(fontBold, 11);
            cs.newLineAtOffset(margin, y);
            cs.showText("Summary");
            cs.endText();
            y -= 18;

            String[] summaryLines = {
                "Total Hot Desk Bookings:  " + totalHotDesk,
                "Total Meeting Room Bookings:  " + totalMeeting,
                "Total Private Room Bookings:  " + totalPrivate,
                "Grand Total Bookings:  " + totalAll,
                String.format(
                    "Grand Total Revenue:  PKR %.2f", totalRevenue)
            };
            for (String line : summaryLines) {
                cs.beginText();
                cs.setFont(fontNormal, 9);
                cs.newLineAtOffset(margin + 10, y);
                cs.showText(line);
                cs.endText();
                y -= 15;
            }

            cs.close();
            doc.save(file);
        }
        return file;
    }

    // ─── DOCX Export ───────────────────────────────────────────

    public static File exportDocx(DefaultTableModel model,
                                   File directory) throws IOException {
        File file = new File(directory, baseFileName() + ".docx");

        try (XWPFDocument doc = new XWPFDocument()) {
            // Title
            XWPFParagraph titlePara = doc.createParagraph();
            titlePara.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = titlePara.createRun();
            titleRun.setText(
                "SCMS — Occupancy Report");
            titleRun.setBold(true);
            titleRun.setFontSize(18);
            titleRun.setFontFamily("Segoe UI");
            titleRun.setColor("2D3F6E");

            // Timestamp
            XWPFParagraph tsPara = doc.createParagraph();
            tsPara.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun tsRun = tsPara.createRun();
            tsRun.setText("Generated: " +
                LocalDateTime.now().format(TIMESTAMP_FMT));
            tsRun.setFontSize(9);
            tsRun.setItalic(true);
            tsRun.setColor("666666");

            // Blank line
            doc.createParagraph();

            // Table
            int colCount = model.getColumnCount();
            int rowCount = model.getRowCount();
            XWPFTable table = doc.createTable(
                rowCount + 1, colCount);
            table.setWidth("100%");

            // Style header row
            XWPFTableRow headerRow = table.getRow(0);
            for (int c = 0; c < colCount; c++) {
                XWPFTableCell cell = headerRow.getCell(c);
                setCellText(cell,
                    model.getColumnName(c), true,
                    "2D3F6E", "FFFFFF");
            }

            // Data rows
            int totalHotDesk = 0, totalMeeting = 0,
                totalPrivate = 0, totalAll = 0;
            double totalRevenue = 0;

            for (int r = 0; r < rowCount; r++) {
                XWPFTableRow row = table.getRow(r + 1);
                String bgColor =
                    (r % 2 == 0) ? "F2F2F7" : "FFFFFF";
                for (int c = 0; c < colCount; c++) {
                    Object val = model.getValueAt(r, c);
                    XWPFTableCell cell = row.getCell(c);
                    setCellText(cell,
                        val != null ? val.toString() : "",
                        false, bgColor, "222222");
                }

                totalHotDesk += toInt(model.getValueAt(r, 1));
                totalMeeting += toInt(model.getValueAt(r, 2));
                totalPrivate += toInt(model.getValueAt(r, 3));
                totalAll     += toInt(model.getValueAt(r, 4));
                totalRevenue += toDouble(model.getValueAt(r, 5));
            }

            // Summary section
            doc.createParagraph();
            XWPFParagraph sumTitle = doc.createParagraph();
            XWPFRun sumRun = sumTitle.createRun();
            sumRun.setText("Summary");
            sumRun.setBold(true);
            sumRun.setFontSize(13);
            sumRun.setColor("2D3F6E");

            String[] summaryLines = {
                "Total Hot Desk Bookings:  " + totalHotDesk,
                "Total Meeting Room Bookings:  " + totalMeeting,
                "Total Private Room Bookings:  " + totalPrivate,
                "Grand Total Bookings:  " + totalAll,
                String.format(
                    "Grand Total Revenue:  PKR %.2f", totalRevenue)
            };
            for (String line : summaryLines) {
                XWPFParagraph p = doc.createParagraph();
                p.setIndentationLeft(360);
                XWPFRun run = p.createRun();
                run.setText(line);
                run.setFontSize(10);
                run.setFontFamily("Segoe UI");
            }

            try (FileOutputStream fos =
                     new FileOutputStream(file)) {
                doc.write(fos);
            }
        }
        return file;
    }

    // ─── Helpers ───────────────────────────────────────────────

    private static void setCellText(XWPFTableCell cell,
                                     String text,
                                     boolean bold,
                                     String bgColor,
                                     String textColor) {
        // Set shading / background color
        CTTcPr tcPr = cell.getCTTc().addNewTcPr();
        CTShd shd = tcPr.addNewShd();
        shd.setFill(bgColor);
        shd.setVal(STShd.CLEAR);

        // Clear default empty paragraph
        cell.removeParagraph(0);
        XWPFParagraph p = cell.addParagraph();
        p.setSpacingBefore(40);
        p.setSpacingAfter(40);

        XWPFRun run = p.createRun();
        run.setText(text);
        run.setBold(bold);
        run.setFontSize(9);
        run.setFontFamily("Segoe UI");
        run.setColor(textColor);
    }

    private static String padRight(String s, int width) {
        if (s == null) s = "";
        if (s.length() >= width) return s.substring(0, width);
        return s + " ".repeat(width - s.length());
    }

    private static int toInt(Object o) {
        if (o == null) return 0;
        if (o instanceof Number) return ((Number) o).intValue();
        try { return Integer.parseInt(o.toString().trim()); }
        catch (NumberFormatException e) { return 0; }
    }

    private static double toDouble(Object o) {
        if (o == null) return 0;
        if (o instanceof Number)
            return ((Number) o).doubleValue();
        try { return Double.parseDouble(o.toString().trim()); }
        catch (NumberFormatException e) { return 0; }
    }
}
