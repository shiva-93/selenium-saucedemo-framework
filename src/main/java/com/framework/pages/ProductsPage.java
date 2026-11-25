package com.framework.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SauceDemo inventory / products page.
 * <p>
 * WHY POM: product actions hide brittle CSS (add-to-cart id slugs, badge).
 * WHY fluent: {@link #openCart()} returns {@link CartPage} for readable purchase flows.
 */
public class ProductsPage extends BasePage {

    private final By inventoryContainer = By.id("inventory_container");
    private final By productNames = By.cssSelector(".inventory_item_name");
    private final By productPrices = By.cssSelector(".inventory_item_price");
    private final By cartBadge = By.cssSelector(".shopping_cart_badge");
    private final By cartLink = By.cssSelector(".shopping_cart_link");
    private final By sortDropdown = By.cssSelector("select.product_sort_container");

    public ProductsPage() {
        waitForVisible(inventoryContainer);
    }

    @Step("Add product to cart by name '{productName}'")
    public ProductsPage addProductByName(String productName) {
        // SauceDemo button data-test: add-to-cart-<slug> from the product title
        String slug = productName.toLowerCase().replace(' ', '-');
        By addButton = By.cssSelector("[data-test='add-to-cart-" + slug + "']");
        click(addButton);
        return this;
    }

    @Step("Get cart badge count")
    public int getCartBadgeCount() {
        if (!isDisplayed(cartBadge)) {
            return 0;
        }
        return Integer.parseInt(getText(cartBadge));
    }

    @Step("Open shopping cart")
    public CartPage openCart() {
        click(cartLink);
        return new CartPage();
    }

    @Step("Sort products by visible text '{option}'")
    public ProductsPage sortByVisibleText(String option) {
        selectByVisibleText(sortDropdown, option);
        return this;
    }

    @Step("Sort products by price low to high")
    public ProductsPage sortByPriceLowToHigh() {
        return sortByVisibleText("Price (low to high)");
    }

    @Step("Get product names in display order")
    public List<String> getProductNames() {
        return findAll(productNames).stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    @Step("Get product prices in display order")
    public List<Double> getProductPricesInOrder() {
        return findAll(productPrices).stream()
                .map(WebElement::getText)
                .map(text -> text.replace("$", "").trim())
                .map(Double::parseDouble)
                .collect(Collectors.toList());
    }
}
