package com.framework.listeners;

import com.framework.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TestNG retry analyzer driven by {@code retryCount} from config.
 * <p>
 * WHY ConcurrentHashMap keyed by method+params: parallel methods each need
 * an independent counter — a single instance field would race across threads.
 * <p>
 * Allure + retries: TestNG re-invokes the method after a failure. Allure TestNG
 * records each attempt; a later pass does not erase the earlier failed result
 * (correct — flaky history stays visible). Extent creates a fresh node per
 * attempt via {@link TestListener}. Do not force the failed attempt to PASS.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger LOG = LogManager.getLogger(RetryAnalyzer.class);
    private static final ConcurrentHashMap<String, AtomicInteger> ATTEMPTS = new ConcurrentHashMap<>();

    @Override
    public boolean retry(ITestResult result) {
        int maxRetries = ConfigReader.getRetryCount();
        if (maxRetries <= 0) {
            return false;
        }
        String key = keyOf(result);
        int attempt = ATTEMPTS.computeIfAbsent(key, k -> new AtomicInteger(0)).incrementAndGet();
        if (attempt <= maxRetries) {
            LOG.warn("Retrying {} (attempt {} of {})", key, attempt, maxRetries);
            return true;
        }
        ATTEMPTS.remove(key);
        return false;
    }

    static void clear(ITestResult result) {
        ATTEMPTS.remove(keyOf(result));
    }

    private static String keyOf(ITestResult result) {
        return result.getMethod().getQualifiedName()
                + Arrays.toString(result.getParameters());
    }
}
