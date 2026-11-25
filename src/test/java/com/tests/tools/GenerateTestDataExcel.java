package com.tests.tools;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * One-shot generator for {@code src/test/resources/testdata/TestData.xlsx}.
 * Run: {@code mvn -q exec:java -Dexec.classpathScope=test
 * -Dexec.mainClass=com.tests.tools.GenerateTestDataExcel}
 */
public final class GenerateTestDataExcel {

    public static void main(String[] args) throws Exception {
        Path out = Paths.get("src/test/resources/testdata/TestData.xlsx");
        Files.createDirectories(out.getParent());

        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet login = wb.createSheet("Login");
            writeRow(login, 0, "username", "password", "expectedOutcome");
            writeRow(login, 1, "standard_user", "secret_sauce", "success");
            writeRow(login, 2, "locked_out_user", "secret_sauce", "locked");
            writeRow(login, 3, "problem_user", "secret_sauce", "success");
            writeRow(login, 4, "performance_glitch_user", "secret_sauce", "success");
            writeRow(login, 5, "error_user", "secret_sauce", "success");
            writeRow(login, 6, "visual_user", "secret_sauce", "success");
            writeRow(login, 7, "invalid_user", "wrong_password", "failure");

            Sheet checkout = wb.createSheet("Checkout");
            writeRow(checkout, 0, "firstName", "lastName", "postalCode");
            writeRow(checkout, 1, "John", "Doe", "12345");
            writeRow(checkout, 2, "Jane", "Smith", "90210");
            writeRow(checkout, 3, "Alex", "Taylor", "10001");

            try (OutputStream os = Files.newOutputStream(out)) {
                wb.write(os);
            }
        }
        System.out.println("Wrote " + out.toAbsolutePath());
    }

    private static void writeRow(Sheet sheet, int rowIndex, String... values) {
        Row row = sheet.createRow(rowIndex);
        for (int i = 0; i < values.length; i++) {
            row.createCell(i).setCellValue(values[i]);
        }
    }
}
