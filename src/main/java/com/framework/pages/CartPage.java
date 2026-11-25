package com.framework.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SauceDemo cart.
 * <p>
 * WHY fluent: {@link #checkout()} returns step-one {@link CheckoutPage} so the
 * happy-path chain stays linear without leaking URLs or locators into tests.
 */
public class CartPage extends BasePage {

    private final By cartList = By.cssSelector(".cart_list");
    private final By itemNames = By.cssSelector(".cart_item .inventory_item_name");
    private final By checkoutButton = By.id("checkout");

    public CartPage() {
        waitForVisible(cartList);
    }

    @Step("Get cart item names")
    public List<String> getItemNames() {
        return findAll(itemNames).stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    @Step("Proceed to checkout")
    public CheckoutPage checkout() {
        click(checkoutButton);
        return new CheckoutPage();
    }
}
