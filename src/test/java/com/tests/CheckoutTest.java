package com.tests;

import com.framework.models.CheckoutInfo;
import com.framework.pages.CheckoutPage;
import com.framework.pages.LoginPage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("SauceDemo")
@Feature("Checkout")
@Owner("shiva-93")
public class CheckoutTest extends BaseTest {

    @Test
    @Story("Complete purchase")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Happy-path checkout ends with the SauceDemo thank-you confirmation")
    public void completeCheckoutShowsThankYou() {
        CheckoutInfo info = CheckoutInfo.builder()
                .firstName("John")
                .lastName("Doe")
                .postalCode("12345")
                .build();

        CheckoutPage complete = new LoginPage()
                .loginAs("standard_user", "secret_sauce")
                .addProductByName("Sauce Labs Backpack")
                .openCart()
                .checkout()
                .fillAndContinue(info)
                .finish();

        assertThat(complete.isThankYouVisible()).isTrue();
        assertThat(complete.getConfirmationMessage())
                .containsIgnoringCase("Thank you for your order");
    }
}
