package com.baarsch_bytes.studentRegDemo.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/**
 * A Page Object for the Students page. Tests call methods named after what a user does,
 * and only this class knows how to find elements. When the page changes, you fix one class
 * instead of every test.
 *
 * This is a starting point: extend it, and write a similar class for the Courses page.
 */
public class StudentListPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String baseUrl;

    private static final By TABLE = By.id("student-list-table");
    private static final By NAME_INPUT = By.id("new-student-name");
    private static final By MAJOR_INPUT = By.id("new-student-major");
    private static final By GPA_INPUT = By.id("new-student-gpa");
    private static final By ADD_BUTTON = By.cssSelector("#add-student-fields button");

    public StudentListPage(WebDriver driver, WebDriverWait wait, String baseUrl) {
        this.driver = driver;
        this.wait = wait;
        this.baseUrl = baseUrl;
    }

    /** Opens the page and waits until the student table has loaded. */
    public StudentListPage open() {
        driver.get(baseUrl + "/students");
        wait.until(ExpectedConditions.visibilityOfElementLocated(TABLE));
        return this;
    }

    /** Fills in the new-student form and submits it. Doesn't wait for the result. */
    public void submitNewStudent(String name, String major, String gpa) {
        type(NAME_INPUT, name);
        type(MAJOR_INPUT, major);
        type(GPA_INPUT, gpa);
        driver.findElement(ADD_BUTTON).click();
    }

    /**
     * Waits until a row for the student with this exact name appears, and returns its id.
     * Rows are identified by database id, which a test can't know in advance, so we find
     * the row through its name cell.
     */
    public long waitForStudentNamed(String name) {
        By nameCell = By.xpath("//td[starts-with(@id,'student-name-') and normalize-space(.)="
                + xpathLiteral(name) + "]");
        WebElement cell = wait.until(ExpectedConditions.visibilityOfElementLocated(nameCell));
        return Long.parseLong(cell.getDomAttribute("id").substring("student-name-".length()));
    }

    public String nameOf(long id) {
        return driver.findElement(By.id("student-name-" + id)).getText();
    }

    public String majorOf(long id) {
        return driver.findElement(By.id("student-major-" + id)).getText();
    }

    public String gpaOf(long id) {
        return driver.findElement(By.id("student-gpa-" + id)).getText();
    }

    public List<WebElement> rows() {
        return driver.findElements(By.cssSelector("#student-list-table tbody tr[id^='student-row-']"));
    }

    private void type(By locator, String text) {
        WebElement input = driver.findElement(locator);
        input.clear();
        input.sendKeys(text);
    }

    /** Quotes text for use in an XPath expression, even if it contains quote marks. */
    private static String xpathLiteral(String text) {
        if (!text.contains("'")) {
            return "'" + text + "'";
        }
        return "concat('" + text.replace("'", "', \"'\", '") + "')";
    }
}
