package com.tests;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("SauceDemo")
@Feature("Products")
@Owner("shiva-93")
public class ProductsTest extends BaseTest {

    @Test
    @Story("Sort by price")
    @Severity(SeverityLevel.NORMAL)
    @Description("Price (low to high) sorts inventory prices in non-decreasing order")
    public void sortByPriceLowToHighIsAscending() {
        ProductsPage products = new LoginPage()
                .loginAs("standard_user", "secret_sauce")
                .sortByPriceLowToHigh();

        List<Double> prices = products.getProductPricesInOrder();
        List<Double> sorted = new ArrayList<>(prices);
        Collections.sort(sorted);

        assertThat(prices)
                .as("Displayed prices should be ascending after low-to-high sort")
                .isEqualTo(sorted)
                .isNotEmpty();
    }
}
