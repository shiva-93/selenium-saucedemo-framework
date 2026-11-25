package com.framework.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Parallel-safe ExtentReports holder.
 * <p>
 * WHY Singleton {@link ExtentReports} + {@link ThreadLocal}{@link ExtentTest}:
 * one HTML report file, but each parallel TestNG method owns its own node.
 */
public final class ExtentManager {

    private static final Object LOCK = new Object();
    private static volatile ExtentReports extent;
    private static final ThreadLocal<ExtentTest> TEST = new ThreadLocal<>();
    private static final Path REPORT_DIR = Paths.get("target", "extent-reports");

    private ExtentManager() {
    }

    public static ExtentReports getInstance() {
        if (extent == null) {
            synchronized (LOCK) {
                if (extent == null) {
                    extent = createInstance();
                }
            }
        }
        return extent;
    }

    private static ExtentReports createInstance() {
        try {
            Files.createDirectories(REPORT_DIR);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot create extent report directory", e);
        }
        String fileName = "ExtentReport_"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                + ".html";
        Path reportPath = REPORT_DIR.resolve(fileName);

        ExtentSparkReporter spark = new ExtentSparkReporter(reportPath.toString());
        spark.config().setTheme(Theme.STANDARD);
        spark.config().setDocumentTitle("SauceDemo Automation");
        spark.config().setReportName("Selenium SauceDemo Suite");

        ExtentReports reports = new ExtentReports();
        reports.attachReporter(spark);
        reports.setSystemInfo("Project", "selenium-saucedemo-framework");
        return reports;
    }

    public static ExtentTest createTest(String name, String description) {
        ExtentTest test = getInstance().createTest(name, description);
        TEST.set(test);
        return test;
    }

    public static ExtentTest getTest() {
        return TEST.get();
    }

    public static void setTest(ExtentTest test) {
        TEST.set(test);
    }

    public static void removeTest() {
        TEST.remove();
    }

    public static void flush() {
        if (extent != null) {
            synchronized (LOCK) {
                if (extent != null) {
                    extent.flush();
                }
            }
        }
    }
}
