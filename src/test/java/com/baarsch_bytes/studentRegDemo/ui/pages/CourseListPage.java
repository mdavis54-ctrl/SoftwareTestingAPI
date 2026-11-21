
/* THIS PAGE WAS WRITTEN AS PART OF THE AI ASSISTANCE PORTION OF LAB 7 */

package com.baarsch_bytes.studentRegDemo.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CourseListPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String baseUrl;

    // Course table and creation form
    private static final By TABLE =
            By.id("course-list-table");

    private static final By COURSE_NAME_INPUT =
            By.id("new-course-name");

    private static final By INSTRUCTOR_INPUT =
            By.id("new-course-instructor");

    private static final By MAX_SIZE_INPUT =
            By.id("new-course-max-size");

    private static final By ROOM_INPUT =
            By.id("new-course-room");

    private static final By ADD_BUTTON =
            By.cssSelector("#new-course-fields button");

    // Course edit form
    private static final By EDIT_NAME =
            By.id("edit-course-name");

    private static final By EDIT_INSTRUCTOR =
            By.id("edit-course-instructor");

    private static final By EDIT_MAX_SIZE =
            By.id("edit-course-max-size");

    private static final By EDIT_ROOM =
            By.id("edit-course-room");

    private static final By SAVE_BUTTON =
            By.id("edit-course-save-button");

    private static final By CANCEL_BUTTON =
            By.id("edit-course-cancel-button");

    // Student removal controls
    private static final By REMOVE_STUDENT_SELECT =
            By.id("remove-student-select");

    private static final By REMOVE_STUDENT_BUTTON =
            By.id("remove-student-button");

    public CourseListPage(
            WebDriver driver,
            WebDriverWait wait,
            String baseUrl
    ) {
        this.driver = driver;
        this.wait = wait;
        this.baseUrl = baseUrl;
    }

    // Opens the course list page.
    public CourseListPage open() {
        driver.get(baseUrl + "/");

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(TABLE)
        );

        return this;
    }

    // Creates a new course.
    public void submitNewCourse(
            String name,
            String instructorId,
            String maxSize,
            String room
    ) {
        type(COURSE_NAME_INPUT, name);
        type(INSTRUCTOR_INPUT, instructorId);
        type(MAX_SIZE_INPUT, maxSize);
        type(ROOM_INPUT, room);

        wait.until(
                ExpectedConditions.elementToBeClickable(ADD_BUTTON)
        ).click();
    }

    // Waits for a course with the specified name and returns its ID.
    public long waitForCourseNamed(String name) {
        By nameCell = By.xpath(
                "//td[starts-with(@id,'course-name-') and normalize-space(.)="
                        + xpathLiteral(name) + "]"
        );

        WebElement cell = wait.until(
                ExpectedConditions.visibilityOfElementLocated(nameCell)
        );

        String cellId = cell.getDomAttribute("id");

        return Long.parseLong(
                cellId.substring("course-name-".length())
        );
    }

    // Returns the course name.
    public String nameOf(long id) {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("course-name-" + id)
                )
        ).getText();
    }

    // Returns the instructor ID.
    public String instructorOf(long id) {
        return driver.findElement(
                By.id("course-instructor-" + id)
        ).getText();
    }

    // Returns the maximum course size.
    public String maxSizeOf(long id) {
        return driver.findElement(
                By.id("course-max-size-" + id)
        ).getText();
    }

    // Returns the room.
    public String roomOf(long id) {
        return driver.findElement(
                By.id("course-room-" + id)
        ).getText();
    }

    // Returns the enrolled student count.
    public String enrolledCountOf(long id) {
        return driver.findElement(
                By.id("course-roster-" + id)
        ).getText();
    }

    // Returns the course row.
    public WebElement courseRow(long id) {
        return driver.findElement(
                By.id("course-row-" + id)
        );
    }

    // Checks whether a course currently exists in the table.
    public boolean courseExists(long id) {
        return !driver.findElements(
                By.id("course-row-" + id)
        ).isEmpty();
    }

    // Opens the edit form for a course.
    public void openEdit(long id) {
        WebElement row = courseRow(id);

        row.findElement(
                By.id("edit-course-button")
        ).click();

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(EDIT_NAME)
        );
    }

    // Edits a course and waits for the updated name to appear.
    public void editCourse(
            long id,
            String name,
            String instructorId,
            String maxSize,
            String room
    ) {
        openEdit(id);

        type(EDIT_NAME, name);
        type(EDIT_INSTRUCTOR, instructorId);
        type(EDIT_MAX_SIZE, maxSize);
        type(EDIT_ROOM, room);

        wait.until(
                ExpectedConditions.elementToBeClickable(SAVE_BUTTON)
        ).click();

        wait.until(
                ExpectedConditions.textToBe(
                        By.id("course-name-" + id),
                        name
                )
        );

        wait.until(
                ExpectedConditions.textToBe(
                        By.id("course-instructor-" + id),
                        instructorId
                )
        );

        wait.until(
                ExpectedConditions.textToBe(
                        By.id("course-max-size-" + id),
                        maxSize
                )
        );

        wait.until(
                ExpectedConditions.textToBe(
                        By.id("course-room-" + id),
                        room
                )
        );
    }

    // Changes the edit fields and cancels without saving.
    public void cancelEdit(
            long id,
            String name,
            String instructorId,
            String maxSize,
            String room
    ) {
        openEdit(id);

        type(EDIT_NAME, name);
        type(EDIT_INSTRUCTOR, instructorId);
        type(EDIT_MAX_SIZE, maxSize);
        type(EDIT_ROOM, room);

        wait.until(
                ExpectedConditions.elementToBeClickable(CANCEL_BUTTON)
        ).click();

        wait.until(
                ExpectedConditions.invisibilityOfElementLocated(EDIT_NAME)
        );
    }

    // Deletes a course.
    public void deleteCourse(long id) {
        WebElement row = courseRow(id);

        row.findElement(
                By.id("delete-course-button")
        ).click();

        waitForCourseToDisappear(id);
    }

    // Adds a student to a course.
    public void addStudent(long courseId, String studentName) {
        WebElement row = courseRow(courseId);

        Select students = new Select(
                row.findElement(By.id("select-student"))
        );

        wait.until(driver -> students.getOptions().stream()
                .anyMatch(option ->
                        option.getText().trim().equals(studentName)
                )
        );

        students.selectByVisibleText(studentName);

        row.findElement(
                By.id("add-student-button")
        ).click();
    }

    // Removes a student from the currently edited course.
    public void removeStudent(String studentName) {
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        REMOVE_STUDENT_SELECT
                )
        );

        Select students = new Select(
                driver.findElement(REMOVE_STUDENT_SELECT)
        );

        students.selectByVisibleText(studentName);

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        REMOVE_STUDENT_BUTTON
                )
        ).click();
    }

    // Waits until the enrolled count reaches the expected value.
    public void waitForEnrolledCount(
            long courseId,
            int expectedCount
    ) {
        wait.until(
                ExpectedConditions.textToBe(
                        By.id("course-roster-" + courseId),
                        String.valueOf(expectedCount)
                )
        );
    }

    // Waits for a course row to disappear.
    public void waitForCourseToDisappear(long id) {
        wait.until(
                ExpectedConditions.invisibilityOfElementLocated(
                        By.id("course-row-" + id)
                )
        );
    }

    // Returns the roster cell text.
    public String rosterOf(long id) {
        WebElement row = courseRow(id);

        // The sixth table cell is the roster column.
        return row.findElement(
                By.cssSelector("td:nth-child(6)")
        ).getText();
    }

    // Waits until a student appears in the course roster.
    public void waitForStudentInRoster(
            long courseId,
            String studentName
    ) {
        wait.until(driver ->
                rosterOf(courseId).contains(studentName)
        );
    }

    // Waits until a student is no longer in the course roster.
    public void waitForStudentRemovedFromRoster(
            long courseId,
            String studentName
    ) {
        wait.until(driver ->
                !rosterOf(courseId).contains(studentName)
        );
    }

    // Types into an input after waiting for it to become visible.
    private void type(By locator, String value) {
        WebElement input = wait.until(
                ExpectedConditions.visibilityOfElementLocated(locator)
        );

        input.clear();
        input.sendKeys(value);
    }

    // Safely formats text for an XPath expression.
    private static String xpathLiteral(String text) {
        if (!text.contains("'")) {
            return "'" + text + "'";
        }

        if (!text.contains("\"")) {
            return "\"" + text + "\"";
        }

        String[] parts = text.split("'", -1);
        StringBuilder result = new StringBuilder("concat(");

        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                result.append(", \"'\", ");
            }

            result.append("'").append(parts[i]).append("'");
        }

        result.append(")");

        return result.toString();
    }
}
