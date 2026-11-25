package com.framework.utils;

import com.framework.config.ConfigReader;
import com.framework.driver.DriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.Function;

/**
 * Optional wait helpers for non-page code (listeners, custom flows).
 * <p>
 * WHY: {@link com.framework.pages.BasePage} owns day-to-day waits for POM;
 * this util exists for call sites that are not page objects and need a
 * one-liner WebDriverWait without duplicating timeout wiring.
 */
public final class WaitUtils {

    private WaitUtils() {
    }

    public static WebDriverWait defaultWait() {
        return new WebDriverWait(
                DriverManager.getDriver(),
                Duration.ofSeconds(ConfigReader.getExplicitWaitSeconds()));
    }

    public static WebElement visible(By locator) {
        return defaultWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement clickable(By locator) {
        return defaultWait().until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static boolean invisible(By locator) {
        return defaultWait().until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public static <T> T until(Function<WebDriver, T> condition) {
        return defaultWait().until(condition);
    }
}
