package com.framework.pages;

import com.framework.models.CheckoutInfo;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

/**
 * SauceDemo checkout flow (step one → overview → complete) on one page object.
 * <p>
 * WHY single class: the three screens are a short linear flow; one fluent API
 * avoids ceremony while still encapsulating each step's locators.
 * WHY {@link CheckoutInfo}: typed builder object instead of three loose strings.
 */
public class CheckoutPage extends BasePage {

    // Step one — your information
    private final By firstNameInput = By.id("first-name");
    private final By lastNameInput = By.id("last-name");
    private final By postalCodeInput = By.id("postal-code");
    private final By continueButton = By.id("continue");

    // Step two — overview
    private final By finishButton = By.id("finish");
    private final By summaryInfo = By.cssSelector(".summary_info");

    // Complete
    private final By completeHeader = By.cssSelector(".complete-header");
    private final By completeText = By.cssSelector(".complete-text");

    public CheckoutPage() {
        waitForVisible(firstNameInput);
    }

    @Step("Fill checkout info: {info}")
    public CheckoutPage fillInfo(CheckoutInfo info) {
        type(firstNameInput, info.getFirstName());
        type(lastNameInput, info.getLastName());
        type(postalCodeInput, info.getPostalCode());
        return this;
    }

    @Step("Continue to checkout overview")
    public CheckoutPage continueToOverview() {
        click(continueButton);
        waitForVisible(summaryInfo);
        return this;
    }

    @Step("Fill checkout info and continue")
    public CheckoutPage fillAndContinue(CheckoutInfo info) {
        return fillInfo(info).continueToOverview();
    }

    @Step("Finish checkout")
    public CheckoutPage finish() {
        click(finishButton);
        waitForVisible(completeHeader);
        return this;
    }

    @Step("Get confirmation message")
    public String getConfirmationMessage() {
        return getText(completeHeader);
    }

    @Step("Get confirmation details text")
    public String getConfirmationDetails() {
        return getText(completeText);
    }

    @Step("Is thank-you confirmation visible")
    public boolean isThankYouVisible() {
        return isDisplayed(completeHeader)
                && getConfirmationMessage().toLowerCase().contains("thank you");
    }
}
