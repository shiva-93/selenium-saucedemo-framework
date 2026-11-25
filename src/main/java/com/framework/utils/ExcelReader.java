package com.framework.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Reads Excel sheets into {@code Object[][]} for TestNG DataProviders.
 * <p>
 * WHY: keep spreadsheet parsing out of tests; DataProviders only pick a sheet.
 * Handles STRING / NUMERIC / BOOLEAN / blank via {@link DataFormatter} + cell type.
 */
public final class ExcelReader {

    private static final DataFormatter FORMATTER = new DataFormatter();

    private ExcelReader() {
    }

    /**
     * @param classpathResource e.g. {@code testdata/TestData.xlsx}
     * @param sheetName         sheet to read (header row skipped)
     */
    public static Object[][] readSheet(String classpathResource, String sheetName) {
        try (InputStream in = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(classpathResource)) {
            if (in == null) {
                throw new IllegalStateException("Excel not found on classpath: " + classpathResource);
            }
            try (Workbook workbook = WorkbookFactory.create(in)) {
                Sheet sheet = workbook.getSheet(sheetName);
                if (sheet == null) {
                    throw new IllegalStateException(
                            "Sheet '" + sheetName + "' not found in " + classpathResource);
                }
                return toDataProviderMatrix(sheet);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed reading " + classpathResource, e);
        }
    }

    private static Object[][] toDataProviderMatrix(Sheet sheet) {
        Iterator<Row> rows = sheet.iterator();
        if (!rows.hasNext()) {
            return new Object[0][0];
        }
        Row header = rows.next();
        int columnCount = header.getLastCellNum();
        List<Object[]> data = new ArrayList<>();

        while (rows.hasNext()) {
            Row row = rows.next();
            if (isRowBlank(row, columnCount)) {
                continue;
            }
            Object[] values = new Object[columnCount];
            for (int c = 0; c < columnCount; c++) {
                values[c] = cellValue(row.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL));
            }
            data.add(values);
        }
        return data.toArray(new Object[0][]);
    }

    private static boolean isRowBlank(Row row, int columnCount) {
        if (row == null) {
            return true;
        }
        for (int c = 0; c < columnCount; c++) {
            Cell cell = row.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell != null && !FORMATTER.formatCellValue(cell).isBlank()) {
                return false;
            }
        }
        return true;
    }

    private static Object cellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case BOOLEAN -> cell.getBooleanCellValue();
            case NUMERIC -> {
                // Keep integers without ".0" for usernames/codes typed as numbers
                double n = cell.getNumericCellValue();
                if (n == Math.rint(n)) {
                    yield String.valueOf((long) n);
                }
                yield n;
            }
            case FORMULA -> FORMATTER.formatCellValue(cell).trim();
            case BLANK -> "";
            default -> FORMATTER.formatCellValue(cell).trim();
        };
    }
}
