package com.baarsch_bytes.studentRegDemo.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Shared setup for UI tests. Extend this class, and name test classes *IT.java so that
 * `mvn verify` runs them against a freshly started app.
 *
 * System properties (set in pom.xml, or with -D on the command line):
 *   app.url   where the app is running (default http://localhost:8080)
 *   headless  true to run Chrome without a window (default true); use false to watch
 */
@ExtendWith(UiTestBase.ScreenshotOnFailure.class)
public abstract class UiTestBase {

    protected static final String BASE_URL = System.getProperty("app.url", "http://localhost:8080");
    protected static final Duration DEFAULT_WAIT = Duration.ofSeconds(10);

    private static final AtomicInteger counter = new AtomicInteger();

    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeEach
    void startBrowser() {
        ChromeOptions options = new ChromeOptions();
        if (Boolean.parseBoolean(System.getProperty("headless", "true"))) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1400,1000");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, DEFAULT_WAIT);
    }

    @AfterEach
    void stopBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Returns a name no other test run has used, such as "Ada 1731700000-3".
     * The database keeps everything until the app restarts, so tests that create data
     * should use unique names rather than assume an empty database.
     */
    protected static String unique(String prefix) {
        return prefix + " " + System.currentTimeMillis() + "-" + counter.incrementAndGet();
    }

    /** Saves a screenshot to target/ui-screenshots when a test fails. */
    static class ScreenshotOnFailure implements AfterTestExecutionCallback {
        @Override
        public void afterTestExecution(ExtensionContext context) {
            if (context.getExecutionException().isEmpty()) {
                return;
            }
            Object instance = context.getRequiredTestInstance();
            if (!(instance instanceof UiTestBase test) || test.driver == null) {
                return;
            }
            try {
                Path source = ((TakesScreenshot) test.driver).getScreenshotAs(OutputType.FILE).toPath();
                Path target = Path.of("target", "ui-screenshots",
                        context.getRequiredTestClass().getSimpleName() + "-"
                                + context.getRequiredTestMethod().getName() + ".png");
                Files.createDirectories(target.getParent());
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Screenshot saved: " + target.toAbsolutePath());
            } catch (IOException | RuntimeException e) {
                System.out.println("Could not save screenshot: " + e.getMessage());
            }
        }
    }
}
