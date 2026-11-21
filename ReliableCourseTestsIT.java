
/* THIS PAGE WAS WRITTEN AS PART OF THE AI ASSISTANCE PORTION OF LAB 7 */

package com.baarsch_bytes.studentRegDemo.ui;

import com.baarsch_bytes.studentRegDemo.ui.pages.CourseListPage;
import com.baarsch_bytes.studentRegDemo.ui.pages.StudentListPage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import static org.junit.jupiter.api.Assertions.*;

class ReliableCourseTestsIT extends UiTestBase {

    // TC-15: Create a course and verify the form clears (P4)
    @Test
    @DisplayName("TC-15: Create a course and clear all course fields")
    void createCourse() {
        CourseListPage page =
                new CourseListPage(driver, wait, BASE_URL).open();

        String name = unique("Course");

        page.submitNewCourse(
                name,
                "12345",
                "30",
                "Science 101"
        );

        long id = page.waitForCourseNamed(name);

        assertEquals(name, page.nameOf(id));
        assertEquals("12345", page.instructorOf(id));
        assertEquals("30", page.maxSizeOf(id));
        assertEquals("Science 101", page.roomOf(id));

        // P4 returns before clearing these four inputs.
        assertEquals("", inputValue("new-course-name"),
                "Course name should clear after successful creation.");

        assertEquals("", inputValue("new-course-instructor"),
                "Instructor should clear after successful creation.");

        assertEquals("", inputValue("new-course-max-size"),
                "Maximum size should clear after successful creation.");

        assertEquals("", inputValue("new-course-room"),
                "Room should clear after successful creation.");
    }

    // TC-16: View courses
    @Test
    @DisplayName("TC-16: View course information and roster")
    void viewCourses() {
        StudentListPage studentPage =
                new StudentListPage(driver, wait, BASE_URL).open();

        String studentName = unique("Roster Student");

        studentPage.submitNewStudent(
                studentName,
                "Computer Science",
                "3.5"
        );

        studentPage.waitForStudentNamed(studentName);

        CourseListPage page =
                new CourseListPage(driver, wait, BASE_URL).open();

        String courseName = unique("View Course");

        page.submitNewCourse(
                courseName,
                "12345",
                "10",
                "Science 102"
        );

        long id = page.waitForCourseNamed(courseName);

        page.addStudent(id, studentName);
        page.waitForEnrolledCount(id, 1);

        assertEquals(courseName, page.nameOf(id));
        assertEquals("12345", page.instructorOf(id));
        assertEquals("10", page.maxSizeOf(id));
        assertEquals("Science 102", page.roomOf(id));
        assertEquals("1", page.enrolledCountOf(id));

        WebElement rosterCell = driver.findElement(
                By.cssSelector("#course-row-" + id + " td:nth-child(6)")
        );

        assertTrue(rosterCell.getText().contains(studentName),
                "The enrolled student should appear in the course roster.");
    }

    // TC-17: Edit a course
    @Test
    @DisplayName("TC-17: Edit a course room")
    void editCourse() {
        CourseListPage page =
                new CourseListPage(driver, wait, BASE_URL).open();

        String name = unique("Edit Course");

        page.submitNewCourse(
                name,
                "12345",
                "20",
                "Room 101"
        );

        long id = page.waitForCourseNamed(name);

        page.editCourse(
                id,
                name,
                "12345",
                "20",
                "Room 202"
        );

        assertEquals(name, page.nameOf(id));
        assertEquals("12345", page.instructorOf(id));
        assertEquals("20", page.maxSizeOf(id));
        assertEquals("Room 202", page.roomOf(id));
    }

    // TC-18: Delete a course
    @Test
    @DisplayName("TC-18: Delete a course")
    void deleteCourse() {
        CourseListPage page =
                new CourseListPage(driver, wait, BASE_URL).open();

        String name = unique("Delete Course");

        page.submitNewCourse(
                name,
                "12345",
                "20",
                "Room 303"
        );

        long id = page.waitForCourseNamed(name);

        page.deleteCourse(id);

        page.waitForCourseToDisappear(id);

        assertFalse(page.courseExists(id),
                "The deleted course should no longer appear.");
    }

    // TC-19: Cancel course edit
    @Test
    @DisplayName("TC-19: Canceling a course edit preserves original information")
    void cancelCourseEdit() {
        CourseListPage page =
                new CourseListPage(driver, wait, BASE_URL).open();

        String name = unique("Cancel Course");

        page.submitNewCourse(
                name,
                "12345",
                "15",
                "Room 404"
        );

        long id = page.waitForCourseNamed(name);

        String changedName = unique("Changed Course");

        page.cancelEdit(
                id,
                changedName,
                "99999",
                "50",
                "Room 505"
        );

        assertEquals(name, page.nameOf(id));
        assertEquals("12345", page.instructorOf(id));
        assertEquals("15", page.maxSizeOf(id));
        assertEquals("Room 404", page.roomOf(id));

        assertFalse(driver.findElement(
                        By.id("course-row-" + id)
                ).getText().contains(changedName),
                "The changed course name should not appear after canceling.");
    }

    // TC-20: Add to the correct course and remove from its roster (P3 and P6)
    @Test
    @DisplayName("TC-20: Students are added to and removed from the correct course")
    void addStudentToCourse() {
        StudentListPage studentPage =
                new StudentListPage(driver, wait, BASE_URL).open();

        String firstStudent = unique("First Enrollment Student");
        String secondStudent = unique("Second Enrollment Student");

        studentPage.submitNewStudent(
                firstStudent,
                "Computer Science",
                "3.1"
        );

        studentPage.waitForStudentNamed(firstStudent);

        studentPage.submitNewStudent(
                secondStudent,
                "Physics",
                "3.8"
        );

        studentPage.waitForStudentNamed(secondStudent);

        CourseListPage coursePage =
                new CourseListPage(driver, wait, BASE_URL).open();

        String firstCourse = unique("First Enrollment Course");
        String secondCourse = unique("Second Enrollment Course");

        coursePage.submitNewCourse(
                firstCourse,
                "12345",
                "20",
                "Room 606"
        );

        long firstCourseId = coursePage.waitForCourseNamed(firstCourse);

        coursePage.submitNewCourse(
                secondCourse,
                "12345",
                "20",
                "Room 707"
        );

        long secondCourseId = coursePage.waitForCourseNamed(secondCourse);

        assertEquals("0", coursePage.enrolledCountOf(firstCourseId));
        assertEquals("0", coursePage.enrolledCountOf(secondCourseId));

        /*
         * P3 uses courses[0].id instead of the courseId passed to
         * handleAddStudent().
         *
         * Find the first displayed course, which should correspond to
         * courses[0], and leave a selection in its dropdown.
         */
        WebElement firstDisplayedCourse = driver.findElements(
                By.cssSelector("#course-list-table tbody tr[id^='course-row-']")
        ).get(0);

        String firstDisplayedRowId =
                firstDisplayedCourse.getAttribute("id");

        long sourceCourseId = Long.parseLong(
                firstDisplayedRowId.replace("course-row-", "")
        );

        // Choose one of our new courses as the target, ensuring it is not
        // the first displayed course.
        long targetCourseId =
                sourceCourseId == firstCourseId
                        ? secondCourseId
                        : firstCourseId;

        // Leave the first student selected in the first displayed course.
        WebElement sourceRow =
                driver.findElement(By.id("course-row-" + sourceCourseId));

        Select sourceSelect = new Select(
                sourceRow.findElement(By.id("select-student"))
        );

        sourceSelect.selectByVisibleText(firstStudent);

        // Select a DIFFERENT student in the target course.
        WebElement targetRow =
                driver.findElement(By.id("course-row-" + targetCourseId));

        Select targetSelect = new Select(
                targetRow.findElement(By.id("select-student"))
        );

        targetSelect.selectByVisibleText(secondStudent);

        // Add the second student to the target course.
        targetRow.findElement(By.id("add-student-button")).click();

        // P3 should cause this assertion to fail because it uses the
        // first displayed course's selected student instead.
        wait.until(d ->
                "1".equals(coursePage.enrolledCountOf(targetCourseId))
        );

        assertEquals("1", coursePage.enrolledCountOf(targetCourseId),
                "The target course should contain exactly one student.");

        assertEquals("0", coursePage.enrolledCountOf(sourceCourseId),
                "Selecting a student in another course must not enroll them there.");

        WebElement targetRoster = driver.findElement(
                By.cssSelector("#course-row-" + targetCourseId + " td:nth-child(6)")
        );

        assertTrue(targetRoster.getText().contains(secondStudent),
                "The target course should contain the selected second student.");

        assertFalse(targetRoster.getText().contains(firstStudent),
                "The target course should not contain the first course's selected student.");

        /*
         * Remove the second student from the target course.
         * The edit form is rendered in a separate row after the course row.
         */
        targetRow.findElement(By.id("edit-course-button")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("remove-student-select")
        ));

        Select removeSelect = new Select(
                driver.findElement(By.id("remove-student-select"))
        );

        removeSelect.selectByVisibleText(secondStudent);

        driver.findElement(By.id("remove-student-button")).click();

        /*
         * P6 skips loadCourses(), so the displayed enrollment count and
         * roster will remain stale. This wait checks for the updated UI
         * without manually refreshing the page.
         */
        wait.until(d ->
                "0".equals(coursePage.enrolledCountOf(targetCourseId))
        );

        assertEquals("0", coursePage.enrolledCountOf(targetCourseId),
                "The enrollment count should update after removing a student.");

        WebElement updatedRoster = driver.findElement(
                By.cssSelector("#course-row-" + targetCourseId + " td:nth-child(6)")
        );

        assertFalse(updatedRoster.getText().contains(secondStudent),
                "The removed student should no longer appear in the roster.");

        assertEquals("0", coursePage.enrolledCountOf(sourceCourseId),
                "Removing a student from the target course must not affect the source course.");
    }

    // Helper: returns the current value of an input field.
    private String inputValue(String id) {
        return driver.findElement(By.id(id)).getDomProperty("value");
    }
}
