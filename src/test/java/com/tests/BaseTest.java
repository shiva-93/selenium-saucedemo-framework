package com.tests;

import com.framework.config.ConfigReader;
import com.framework.driver.DriverFactory;
import com.framework.driver.DriverManager;
import com.framework.reports.AllureEnvironmentWriter;
import com.framework.reports.ExtentManager;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

/**
 * Shared TestNG lifecycle for feature tests.
 * <p>
 * WHY: tests stay assertion-only — driver creation, base URL, and report env
 * wiring live here so every method starts on a clean SauceDemo session.
 */
public abstract class BaseTest {

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        AllureEnvironmentWriter.write();
    }

    @Parameters({"browser"})
    @BeforeMethod(alwaysRun = true)
    public void setUp(@Optional String browser) {
        // Suite/XML parameter overrides file config for this JVM (ConfigReader prefers system props)
        if (browser != null && !browser.isBlank()) {
            System.setProperty("browser", browser.trim());
        }
        DriverManager.setDriver(DriverFactory.createDriver());
        DriverManager.getDriver().get(ConfigReader.getBaseUrl());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverManager.quitDriver();
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        ExtentManager.flush();
    }
}
