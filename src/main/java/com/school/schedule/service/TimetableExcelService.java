package com.school.schedule.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class TimetableExcelService {

    public byte[] export(Matrix m) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("Thời khóa biểu");

            Font bold = wb.createFont();
            bold.setBold(true);
            Font big = wb.createFont();
            big.setBold(true);
            big.setFontHeightInPoints((short) 14);

            CellStyle title = wb.createCellStyle();
            title.setFont(big);
            title.setAlignment(HorizontalAlignment.CENTER);

            CellStyle header = bordered(wb);
            header.setFont(bold);
            header.setAlignment(HorizontalAlignment.CENTER);
            header.setVerticalAlignment(VerticalAlignment.CENTER);
            header.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            header.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle body = bordered(wb);
            body.setWrapText(true);
            body.setAlignment(HorizontalAlignment.CENTER);
            body.setVerticalAlignment(VerticalAlignment.CENTER);

            int periods = m.getPeriods();
            int cols = m.getDays().size() * periods;

            // Title row
            put(sheet.createRow(0), 0, m.getTitle(), title);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, cols));

            // Header rows
            Row h1 = sheet.createRow(1);
            Row h2 = m.isPeriodHeader() ? sheet.createRow(2) : null;
            put(h1, 0, m.getCornerLabel(), header);
            if (h2 != null) {
                put(h2, 0, "", header);
                sheet.addMergedRegion(new CellRangeAddress(1, 2, 0, 0));
            }
            for (int d = 0; d < m.getDays().size(); d++) {
                int start = 1 + d * periods;
                for (int p = 0; p < periods; p++) {
                    put(h1, start + p, p == 0 ? m.getDays().get(d) : "", header);
                    if (h2 != null) put(h2, start + p, String.valueOf(p + 1), header);
                }
                if (periods > 1) {
                    sheet.addMergedRegion(new CellRangeAddress(1, 1, start, start + periods - 1));
                }
            }

            // Body
            int first = h2 != null ? 3 : 2;
            for (int r = 0; r < m.getCells().length; r++) {
                Row row = sheet.createRow(first + r);
                row.setHeightInPoints(m.isPeriodHeader() ? 34 : 50);
                put(row, 0, m.getRowLabels().get(r), header);
                for (int c = 0; c < cols; c++) {
                    put(row, 1 + c, m.getCells()[r][c], body);
                }
            }

            sheet.setColumnWidth(0, 14 * 256);
            for (int c = 1; c <= cols; c++) {
                sheet.setColumnWidth(c, (periods > 1 ? 14 : 24) * 256);
            }
            sheet.createFreezePane(1, first);

            // Print nicely: landscape, one page wide
            sheet.getPrintSetup().setLandscape(true);
            sheet.setFitToPage(true);
            sheet.getPrintSetup().setFitWidth((short) 1);
            sheet.getPrintSetup().setFitHeight((short) 0);

            wb.write(out);
            return out.toByteArray();
        }
    }

    private CellStyle bordered(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        s.setBorderTop(BorderStyle.THIN);
        s.setBorderBottom(BorderStyle.THIN);
        s.setBorderLeft(BorderStyle.THIN);
        s.setBorderRight(BorderStyle.THIN);
        return s;
    }

    private void put(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }
}