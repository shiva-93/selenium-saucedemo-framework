package com.framework.driver;

import org.openqa.selenium.WebDriver;

/**
 * Thread-bound WebDriver holder for the suite.
 * <p>
 * WHY: Singleton-style static access + {@link ThreadLocal} so parallel TestNG
 * methods (thread-count &gt; 1) each own a driver and never share sessions.
 */
public final class DriverManager {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
        // holder utility
    }

    public static WebDriver getDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException(
                    "WebDriver not set for this thread. Call setDriver() before getDriver().");
        }
        return driver;
    }

    public static void setDriver(WebDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("WebDriver must not be null");
        }
        DRIVER.set(driver);
    }

    /**
     * Quits the thread's driver (if present) and clears the ThreadLocal.
     */
    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                DRIVER.remove();
            }
        }
    }
}
