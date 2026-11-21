
/* THIS PAGE WAS WRITTEN AS PART OF THE AI ASSISTANCE PORTION OF LAB 7 */

package com.baarsch_bytes.studentRegDemo.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class StudentListPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String baseUrl;

    // Student table and creation form
    private static final By TABLE =
            By.id("student-list-table");

    private static final By NAME_INPUT =
            By.id("new-student-name");

    private static final By MAJOR_INPUT =
            By.id("new-student-major");

    private static final By GPA_INPUT =
            By.id("new-student-gpa");

    private static final By ADD_BUTTON =
            By.id("add-student-button");

    // Student edit form
    private static final By EDIT_NAME =
            By.id("edit-student-name");

    private static final By EDIT_MAJOR =
            By.id("edit-student-major");

    private static final By EDIT_GPA =
            By.id("edit-student-gpa");

    private static final By SAVE_BUTTON =
            By.id("edit-student-save-button");

    private static final By CANCEL_BUTTON =
            By.id("edit-student-cancel-button");

    public StudentListPage(
            WebDriver driver,
            WebDriverWait wait,
            String baseUrl) {

        this.driver = driver;
        this.wait = wait;
        this.baseUrl = baseUrl;
    }

    /** Opens the Students page and waits for its table. */
    public StudentListPage open() {

        driver.get(baseUrl + "/students");

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(TABLE)
        );

        return this;
    }

    /** Creates a student using the new-student form. */
    public void submitNewStudent(
            String name,
            String major,
            String gpa) {

        type(NAME_INPUT, name);
        type(MAJOR_INPUT, major);
        type(GPA_INPUT, gpa);

        driver.findElement(ADD_BUTTON).click();
    }

    /**
     * Waits for a student with the exact name to appear.
     * Returns the student's database ID.
     */
    public long waitForStudentNamed(String name) {

        By nameCell = By.xpath(
                "//td[starts-with(@id,'student-name-') and normalize-space(.)="
                        + xpathLiteral(name) + "]"
        );

        WebElement cell = wait.until(
                ExpectedConditions.visibilityOfElementLocated(nameCell)
        );

        return Long.parseLong(
                cell.getDomAttribute("id")
                        .substring("student-name-".length())
        );
    }

    /** Returns a student's displayed name. */
    public String nameOf(long id) {

        return driver.findElement(
                By.id("student-name-" + id)
        ).getText();
    }

    /** Returns a student's displayed major. */
    public String majorOf(long id) {

        return driver.findElement(
                By.id("student-major-" + id)
        ).getText();
    }

    /** Returns a student's displayed GPA. */
    public String gpaOf(long id) {

        return driver.findElement(
                By.id("student-gpa-" + id)
        ).getText();
    }

    /** Returns the table row belonging to a particular student. */
    public WebElement studentRow(long id) {

        return driver.findElement(
                By.id("student-row-" + id)
        );
    }

    /** Returns all student rows currently displayed. */
    public List<WebElement> rows() {

        return driver.findElements(
                By.cssSelector(
                        "#student-list-table tbody tr[id^='student-row-']"
                )
        );
    }

    /** Opens the edit form for a specific student. */
    public void openEdit(long id) {

        studentRow(id)
                .findElement(By.id("edit-student-button"))
                .click();

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(EDIT_NAME)
        );
    }

    /** Edits a student's name, major, and GPA. */
    public void editStudent(
            long id,
            String name,
            String major,
            String gpa) {

        openEdit(id);

        type(EDIT_NAME, name);
        type(EDIT_MAJOR, major);
        type(EDIT_GPA, gpa);

        driver.findElement(SAVE_BUTTON).click();

        // Wait for the updated name.
        wait.until(
                ExpectedConditions.textToBe(
                        By.id("student-name-" + id),
                        name
                )
        );

        // Wait for the updated major.
        wait.until(
                ExpectedConditions.textToBe(
                        By.id("student-major-" + id),
                        major
                )
        );

        // Wait for the updated GPA using numerical comparison.
        wait.until(driver -> {
            String actual = gpaOf(id);

            try {
                return Math.abs(
                        Double.parseDouble(actual)
                                - Double.parseDouble(gpa)
                ) < 0.0001;
            } catch (NumberFormatException | org.openqa.selenium.NoSuchElementException e) {
                return false;
            }
        });
    }

    /**
     * Changes the edit form values and cancels without saving.
     */
    public void cancelEdit(
            long id,
            String name,
            String major,
            String gpa) {

        openEdit(id);

        type(EDIT_NAME, name);
        type(EDIT_MAJOR, major);
        type(EDIT_GPA, gpa);

        // Click the Cancel button inside this student's row.
        studentRow(id)
                .findElement(CANCEL_BUTTON)
                .click();

        wait.until(
                ExpectedConditions.invisibilityOfElementLocated(EDIT_NAME)
        );
    }

    /** Deletes a specific student. */
    public void deleteStudent(long id) {

        studentRow(id)
                .findElement(By.id("delete-student-button"))
                .click();

        waitForStudentToDisappear(id);
    }

    /** Waits until a student is no longer displayed. */
    public void waitForStudentToDisappear(long id) {

        wait.until(
                ExpectedConditions.invisibilityOfElementLocated(
                        By.id("student-row-" + id)
                )
        );
    }

    /** Checks whether a student currently exists in the table. */
    public boolean studentExists(long id) {

        return !driver.findElements(
                By.id("student-row-" + id)
        ).isEmpty();
    }

    // New-student form values

    public String nameInputValue() {

        return driver.findElement(NAME_INPUT)
                .getDomProperty("value");
    }

    public String majorInputValue() {

        return driver.findElement(MAJOR_INPUT)
                .getDomProperty("value");
    }

    public String gpaInputValue() {

        return driver.findElement(GPA_INPUT)
                .getDomProperty("value");
    }

    // Edit-student form values

    public String editNameInputValue() {

        return driver.findElement(EDIT_NAME)
                .getDomProperty("value");
    }

    public String editMajorInputValue() {

        return driver.findElement(EDIT_MAJOR)
                .getDomProperty("value");
    }

    public String editGpaInputValue() {

        return driver.findElement(EDIT_GPA)
                .getDomProperty("value");
    }

    /** Fills an input field. */
    private void type(By locator, String value) {

        WebElement input = wait.until(
                ExpectedConditions.visibilityOfElementLocated(locator)
        );

        input.clear();
        input.sendKeys(value);
    }

    /** Safely quotes text for use in an XPath expression. */
    private static String xpathLiteral(String text) {

        if (!text.contains("'")) {
            return "'" + text + "'";
        }

        return "concat('" + text.replace("'", "', \"'\", '") + "')";
    }
}
