package com.framework.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

/**
 * SauceDemo login screen.
 * <p>
 * WHY POM: locators stay private; tests call intent methods.
 * WHY fluent: successful login returns {@link ProductsPage} so chains read like the user journey.
 */
public class LoginPage extends BasePage {

    private final By usernameInput = By.id("user-name");
    private final By passwordInput = By.id("password");
    private final By loginButton = By.id("login-button");
    private final By errorMessage = By.cssSelector("[data-test='error']");

    @Step("Enter username '{username}'")
    public LoginPage enterUsername(String username) {
        type(usernameInput, username);
        return this;
    }

    @Step("Enter password")
    public LoginPage enterPassword(String password) {
        type(passwordInput, password);
        return this;
    }

    @Step("Click login")
    public LoginPage clickLogin() {
        click(loginButton);
        return this;
    }

    /**
     * Valid credentials → inventory. Invalid credentials should use
     * {@link #loginExpectingError(String, String)} instead.
     */
    @Step("Login as {username}")
    public ProductsPage loginAs(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        click(loginButton);
        return new ProductsPage();
    }

    @Step("Login expecting error as {username}")
    public LoginPage loginExpectingError(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        click(loginButton);
        return this;
    }

    @Step("Get login error message")
    public String getErrorMessage() {
        return getText(errorMessage);
    }

    @Step("Is login error displayed")
    public boolean isErrorDisplayed() {
        return isDisplayed(errorMessage);
    }
}
