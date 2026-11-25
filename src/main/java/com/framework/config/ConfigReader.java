package com.framework.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads framework settings once from classpath {@code config.properties}.
 * <p>
 * WHY: centralize env/config so tests never hardcode URLs/timeouts;
 * system properties win over the file so CI can pass {@code -Dbrowser=chrome -Dheadless=true}.
 */
public final class ConfigReader {

    private static final String CONFIG_FILE = "config.properties";
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream stream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(CONFIG_FILE)) {
            if (stream == null) {
                throw new IllegalStateException(
                        "Missing classpath resource: " + CONFIG_FILE
                                + " (expected under src/test/resources)");
            }
            PROPERTIES.load(stream);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(
                    "Failed to load " + CONFIG_FILE + ": " + e.getMessage());
        }
    }

    private ConfigReader() {
        // utility
    }

    public static String getBrowser() {
        return resolve("browser").toLowerCase();
    }

    public static String getBaseUrl() {
        return resolve("baseUrl");
    }

    public static int getExplicitWaitSeconds() {
        return Integer.parseInt(resolve("explicitWaitSeconds"));
    }

    public static int getPageLoadTimeoutSeconds() {
        return Integer.parseInt(resolve("pageLoadTimeoutSeconds"));
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(resolve("headless"));
    }

    public static int getRetryCount() {
        return Integer.parseInt(resolve("retryCount"));
    }

    /**
     * System property overrides file value when present and non-blank.
     */
    private static String resolve(String key) {
        String fromSystem = System.getProperty(key);
        if (fromSystem != null && !fromSystem.isBlank()) {
            return fromSystem.trim();
        }
        String fromFile = PROPERTIES.getProperty(key);
        if (fromFile == null || fromFile.isBlank()) {
            throw new IllegalStateException("Required config key missing: " + key);
        }
        return fromFile.trim();
    }
}
