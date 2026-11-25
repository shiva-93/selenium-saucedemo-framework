package com.framework.pages;

import com.framework.config.ConfigReader;
import com.framework.driver.DriverManager;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Shared Selenium primitives for page objects.
 * <p>
 * WHY: encapsulate explicit waits and common actions once so page objects stay
 * thin and never use {@code Thread.sleep} or implicit waits.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage() {
        this.driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(
                driver, Duration.ofSeconds(ConfigReader.getExplicitWaitSeconds()));
    }

    @Step("Click element {locator}")
    protected void click(By locator) {
        waitForClickable(locator).click();
    }

    @Step("Type into {locator}")
    protected void type(By locator, String text) {
        WebElement element = waitForVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    @Step("Get text from {locator}")
    protected String getText(By locator) {
        return waitForVisible(locator).getText();
    }

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected boolean isDisplayed(By locator) {
        try {
            return waitForVisible(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected List<WebElement> findAll(By locator) {
        waitForVisible(locator);
        return driver.findElements(locator);
    }

    protected void selectByVisibleText(By locator, String visibleText) {
        waitForClickable(locator);
        new org.openqa.selenium.support.ui.Select(driver.findElement(locator))
                .selectByVisibleText(visibleText);
    }
}
