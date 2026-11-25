package com.tests.dataproviders;

import com.framework.utils.ExcelReader;
import org.testng.annotations.DataProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Supplies credentials and negative cases to feature tests.
 * <p>
 * WHY: keep Excel I/O and inline tables out of {@code @Test} methods;
 * {@code parallel = true} lets independent login rows run concurrently.
 */
public final class TestDataProvider {

    private static final String EXCEL = "testdata/TestData.xlsx";

    private TestDataProvider() {
    }

    /**
     * Valid SauceDemo users from the Login sheet ({@code expectedOutcome = success}).
     */
    @DataProvider(name = "validLoginUsers", parallel = true)
    public static Object[][] validLoginUsers() {
        Object[][] rows = ExcelReader.readSheet(EXCEL, "Login");
        List<Object[]> valid = new ArrayList<>();
        for (Object[] row : rows) {
            String outcome = String.valueOf(row[2]).trim().toLowerCase();
            if ("success".equals(outcome)) {
                valid.add(new Object[]{row[0], row[1]});
            }
        }
        return valid.toArray(new Object[0][]);
    }

    /**
     * Inline negative cases: wrong password / unknown user → login error.
     */
    @DataProvider(name = "invalidLoginCredentials", parallel = true)
    public static Object[][] invalidLoginCredentials() {
        return new Object[][]{
                {"standard_user", "wrong_password", "Username and password do not match"},
                {"invalid_user", "secret_sauce", "Username and password do not match"},
                {"", "secret_sauce", "Username is required"},
        };
    }

    @DataProvider(name = "checkoutInfo")
    public static Object[][] checkoutInfo() {
        return ExcelReader.readSheet(EXCEL, "Checkout");
    }
}
