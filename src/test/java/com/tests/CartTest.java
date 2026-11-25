package com.tests;

import com.framework.pages.CartPage;
import com.framework.pages.LoginPage;
import com.framework.pages.ProductsPage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("SauceDemo")
@Feature("Cart")
@Owner("shiva-93")
public class CartTest extends BaseTest {

    private static final String PRODUCT_ONE = "Sauce Labs Backpack";
    private static final String PRODUCT_TWO = "Sauce Labs Bike Light";

    @Test
    @Story("Add products to cart")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Adding two products updates the badge and lists both names in the cart")
    public void addTwoProductsUpdatesBadgeAndCart() {
        ProductsPage products = new LoginPage()
                .loginAs("standard_user", "secret_sauce")
                .addProductByName(PRODUCT_ONE)
                .addProductByName(PRODUCT_TWO);

        assertThat(products.getCartBadgeCount()).isEqualTo(2);

        CartPage cart = products.openCart();
        List<String> items = cart.getItemNames();

        assertThat(items)
                .containsExactlyInAnyOrder(PRODUCT_ONE, PRODUCT_TWO);
    }
}
