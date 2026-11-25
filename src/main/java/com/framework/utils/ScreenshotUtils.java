package com.framework.utils;

import com.framework.driver.DriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Captures PNG screenshots for Extent/Allure attachments and local debugging.
 * <p>
 * WHY: one capture API used by listeners — returns bytes for report attachments
 * and optionally writes under {@code target/screenshots}.
 */
public final class ScreenshotUtils {

    private static final Path SCREENSHOT_DIR = Paths.get("target", "screenshots");
    private static final DateTimeFormatter TS =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private ScreenshotUtils() {
    }

    public static byte[] captureBytes() {
        WebDriver driver = requireDriver();
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    public static Path captureToFile(String namePrefix) {
        return writeBytes(captureBytes(), namePrefix);
    }

    public static Path writeBytes(byte[] bytes, String namePrefix) {
        try {
            Files.createDirectories(SCREENSHOT_DIR);
            String safe = namePrefix == null || namePrefix.isBlank()
                    ? "screenshot"
                    : namePrefix.replaceAll("[^a-zA-Z0-9._-]", "_");
            Path file = SCREENSHOT_DIR.resolve(safe + "_" + LocalDateTime.now().format(TS) + ".png");
            Files.write(file, bytes);
            return file;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write screenshot", e);
        }
    }

    private static WebDriver requireDriver() {
        return DriverManager.getDriver();
    }
}
