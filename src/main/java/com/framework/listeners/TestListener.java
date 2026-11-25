package com.framework.listeners;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.framework.driver.DriverManager;
import com.framework.reports.ExtentManager;
import com.framework.utils.ScreenshotUtils;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Bridges TestNG lifecycle → Log4j2, ExtentReports, and Allure attachments.
 * <p>
 * WHY listener (not BaseTest only): reporting stays out of test classes and
 * still runs for every method under parallel execution.
 */
public class TestListener implements ITestListener {

    private static final Logger LOG = LogManager.getLogger(TestListener.class);

    @Override
    public void onStart(ITestContext context) {
        LOG.info("Suite/test start: {}", context.getName());
        ExtentManager.getInstance();
    }

    @Override
    public void onFinish(ITestContext context) {
        LOG.info("Suite/test finish: {}", context.getName());
        ExtentManager.flush();
    }

    @Override
    public void onTestStart(ITestResult result) {
        String name = displayName(result);
        LOG.info("START: {}", name);
        ExtentManager.createTest(name, result.getMethod().getDescription());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String name = displayName(result);
        LOG.info("PASS: {}", name);
        if (ExtentManager.getTest() != null) {
            ExtentManager.getTest().log(Status.PASS, "Passed");
        }
        RetryAnalyzer.clear(result);
        ExtentManager.removeTest();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String name = displayName(result);
        Throwable error = result.getThrowable();
        LOG.error("FAIL: {} — {}", name, error == null ? "" : error.getMessage(), error);

        if (ExtentManager.getTest() != null) {
            ExtentManager.getTest().log(Status.FAIL, error);
        }

        attachFailureArtifacts(name);
        // Do not clear RetryAnalyzer here — TestNG calls retry() after onTestFailure.
        // Counter is cleared on success or when max retries are exhausted in RetryAnalyzer.
        ExtentManager.removeTest();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String name = displayName(result);
        LOG.warn("SKIP: {}", name);
        if (ExtentManager.getTest() != null) {
            ExtentManager.getTest().log(Status.SKIP,
                    result.getThrowable() == null ? "Skipped" : result.getThrowable().getMessage());
        }
        ExtentManager.removeTest();
    }

    private void attachFailureArtifacts(String testName) {
        try {
            WebDriver driver = DriverManager.getDriver();
            byte[] png = ScreenshotUtils.captureBytes();
            Path file = ScreenshotUtils.writeBytes(png, testName);

            if (ExtentManager.getTest() != null) {
                ExtentManager.getTest().fail(
                        "Screenshot: " + file,
                        MediaEntityBuilder.createScreenCaptureFromPath(file.toAbsolutePath().toString())
                                .build());
            }

            attachScreenshotPng(png);
            Allure.addAttachment("Screenshot", "image/png", new ByteArrayInputStream(png), ".png");
            Allure.addAttachment("Current URL", "text/plain",
                    safe(() -> driver.getCurrentUrl(), "(unavailable)"), ".txt");
            Allure.addAttachment("Page source", "text/html",
                    safe(() -> driver.getPageSource(), "(unavailable)"), ".html");
        } catch (Exception e) {
            LOG.warn("Could not attach failure artifacts: {}", e.getMessage());
        }
    }

    @Attachment(value = "Failure screenshot", type = "image/png")
    private byte[] attachScreenshotPng(byte[] png) {
        return png;
    }

    private static String displayName(ITestResult result) {
        Object[] params = result.getParameters();
        if (params == null || params.length == 0) {
            return result.getMethod().getMethodName();
        }
        return result.getMethod().getMethodName() + " " + Arrays.toString(params);
    }

    private static String safe(SupplierWithException<String> supplier, String fallback) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return fallback;
        }
    }

    @FunctionalInterface
    private interface SupplierWithException<T> {
        T get() throws Exception;
    }
}
