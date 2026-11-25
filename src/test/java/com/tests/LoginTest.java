package com.tests;

import com.framework.pages.LoginPage;
import com.framework.pages.ProductsPage;
import com.tests.dataproviders.TestDataProvider;
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
@Feature("Authentication")
@Owner("shiva-93")
public class LoginTest extends BaseTest {

    @Test(dataProvider = "validLoginUsers", dataProviderClass = TestDataProvider.class)
    @Story("Valid login")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Users marked success in Excel Login sheet reach the products inventory")
    public void validUsersCanLogin(String username, String password) {
        ProductsPage products = new LoginPage().loginAs(username, password);

        assertThat(products.getProductNames())
                .as("Inventory should list products after login as %s", username)
                .isNotEmpty();
    }

    @Test
    @Story("Locked out user")
    @Severity(SeverityLevel.CRITICAL)
    @Description("locked_out_user remains on login with the locked-out error message")
    public void lockedOutUserSeesError() {
        LoginPage login = new LoginPage()
                .loginExpectingError("locked_out_user", "secret_sauce");

        assertThat(login.isErrorDisplayed()).isTrue();
        assertThat(login.getErrorMessage())
                .containsIgnoringCase("locked out");
    }

    @Test(dataProvider = "invalidLoginCredentials", dataProviderClass = TestDataProvider.class)
    @Story("Invalid credentials")
    @Severity(SeverityLevel.NORMAL)
    @Description("Bad password / unknown user / empty username show a login error")
    public void invalidCredentialsShowError(String username, String password, String expectedSnippet) {
        LoginPage login = new LoginPage().loginExpectingError(username, password);

        assertThat(login.isErrorDisplayed()).isTrue();
        assertThat(login.getErrorMessage())
                .containsIgnoringCase(expectedSnippet);
    }
}
