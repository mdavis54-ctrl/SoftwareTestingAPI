
/* THIS PAGE WAS WRITTEN AS PART OF THE AI ASSISTANCE PORTION OF LAB 7 */

package com.baarsch_bytes.studentRegDemo.ui;

import com.baarsch_bytes.studentRegDemo.ui.pages.StudentListPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.*;

class ReliableStudentTestsIT extends UiTestBase {

    // TC-01: Create a student
    @Test
    @DisplayName("TC-01: Create a student")
    void createStudent() {
        StudentListPage page =
                new StudentListPage(driver, wait, BASE_URL).open();

        String name = unique("Alex Smith");
        String major = "Computer Science";

        page.submitNewStudent(name, major, "3.5");

        long id = page.waitForStudentNamed(name);

        assertEquals(name, page.nameOf(id));
        assertEquals(major, page.majorOf(id));
        assertGpaEquals("3.5", page.gpaOf(id));
    }

    // TC-02: View students and verify correct columns (P1)
    @Test
    @DisplayName("TC-02: Student major and GPA appear in their correct columns")
    void viewStudents() {
        StudentListPage page =
                new StudentListPage(driver, wait, BASE_URL).open();

        String name = unique("View Student");
        String major = "Biology";
        String gpa = "3.2";

        page.submitNewStudent(name, major, gpa);

        long id = page.waitForStudentNamed(name);

        assertEquals(name, page.nameOf(id));
        assertEquals(major, page.majorOf(id),
                "The Major column must display the student's major.");

        assertGpaEquals(gpa, page.gpaOf(id),
                "The GPA column must display the student's GPA.");
    }

    // TC-03: Edit a student's major
    @Test
    @DisplayName("TC-03: Edit a student")
    void editStudent() {
        StudentListPage page =
                new StudentListPage(driver, wait, BASE_URL).open();

        String name = unique("Edit Student");
        page.submitNewStudent(name, "Biology", "3.0");

        long id = page.waitForStudentNamed(name);

        page.editStudent(id, name, "Computer Science", "3.0");

        assertEquals(name, page.nameOf(id));
        assertEquals("Computer Science", page.majorOf(id));
        assertGpaEquals("3.0", page.gpaOf(id));
    }

    // TC-04: Delete a selected student instead of the first student (P5)
    @Test
    @DisplayName("TC-04: Deleting a student only removes the selected student")
    void deleteStudent() {
        StudentListPage page =
                new StudentListPage(driver, wait, BASE_URL).open();

        String firstName = unique("Keep Student");
        String secondName = unique("Delete Student");

        page.submitNewStudent(firstName, "Mathematics", "3.1");
        long firstId = page.waitForStudentNamed(firstName);

        page.submitNewStudent(secondName, "Chemistry", "3.8");
        long secondId = page.waitForStudentNamed(secondName);

        assertTrue(page.studentExists(firstId));
        assertTrue(page.studentExists(secondId));

        // Determine which of our two students is NOT first in the displayed list.
        // P5 always deletes the first student in the students array.
        WebElement firstDisplayedRow = page.rows().get(0);
        String firstDisplayedRowId = firstDisplayedRow.getAttribute("id");

        long firstDisplayedId = Long.parseLong(
                firstDisplayedRowId.replace("student-row-", "")
        );

        long targetId;
        long survivorId;

        if (firstDisplayedId == firstId) {
            targetId = secondId;
            survivorId = firstId;
        } else if (firstDisplayedId == secondId) {
            targetId = firstId;
            survivorId = secondId;
        } else {
            // Neither newly created student is first, so either is a valid target.
            targetId = secondId;
            survivorId = firstId;
        }

        page.deleteStudent(targetId);
        page.waitForStudentToDisappear(targetId);

        assertFalse(page.studentExists(targetId),
                "The selected student should be deleted.");

        assertTrue(page.studentExists(survivorId),
                "Deleting one student must not delete another student.");
    }

    // TC-05: GPA minimum boundary (0)
    @Test
    @DisplayName("TC-05: GPA of zero is accepted")
    void minimumGpaIsAccepted() {
        StudentListPage page =
                new StudentListPage(driver, wait, BASE_URL).open();

        String name = unique("Minimum GPA Student");

        page.submitNewStudent(name, "Mathematics", "0");

        long id = page.waitForStudentNamed(name);

        assertEquals(name, page.nameOf(id));
        assertGpaEquals("0", page.gpaOf(id));
    }

    // TC-06: GPA maximum boundary (4.0)
    @Test
    @DisplayName("TC-06: GPA of 4.0 is accepted")
    void maximumGpaIsAccepted() {
        StudentListPage page =
                new StudentListPage(driver, wait, BASE_URL).open();

        String name = unique("Maximum GPA Student");

        page.submitNewStudent(name, "Physics", "4.0");

        long id = page.waitForStudentNamed(name);

        assertEquals(name, page.nameOf(id));
        assertGpaEquals("4.0", page.gpaOf(id));
    }

    // TC-07: GPA below minimum
    @Test
    @DisplayName("TC-07: GPA below zero is rejected")
    void gpaBelowMinimumIsRejected() {
        StudentListPage page =
                new StudentListPage(driver, wait, BASE_URL).open();

        String name = unique("Invalid Low GPA Student");

        page.submitNewStudent(name, "Chemistry", "-0.1");

        assertFalse(studentAppears(page, name),
                "A student with GPA below zero should not be created.");

        assertEquals("-0.1", page.gpaInputValue(),
                "The invalid GPA input should remain available.");
    }

    // TC-08: GPA above maximum
    @Test
    @DisplayName("TC-08: GPA above 4.0 is rejected")
    void gpaAboveMaximumIsRejected() {
        StudentListPage page =
                new StudentListPage(driver, wait, BASE_URL).open();

        String name = unique("Invalid High GPA Student");

        page.submitNewStudent(name, "Physics", "4.1");

        assertFalse(studentAppears(page, name),
                "A student with GPA above 4.0 should not be created.");

        assertEquals("4.1", page.gpaInputValue(),
                "The invalid GPA input should remain available.");
    }

    // TC-09: GPA edit validation
    @Test
    @DisplayName("TC-09: Invalid GPA edit is rejected")
    void invalidGpaEditIsRejected() {
        StudentListPage page =
                new StudentListPage(driver, wait, BASE_URL).open();

        String name = unique("GPA Edit Student");

        page.submitNewStudent(name, "Biology", "3.0");

        long id = page.waitForStudentNamed(name);

        page.openEdit(id);

        driver.findElement(By.id("edit-student-gpa")).clear();
        driver.findElement(By.id("edit-student-gpa")).sendKeys("4.1");

        driver.findElement(By.id("edit-student-save-button")).click();

        assertGpaEquals("3.0", page.gpaOf(id),
                "The original GPA should remain unchanged after an invalid edit.");
    }

    // TC-10: Student name maximum length (255)
    @Test
    @DisplayName("TC-10: Student name of 255 characters is accepted")
    void studentName255CharactersIsAccepted() {
        StudentListPage page =
                new StudentListPage(driver, wait, BASE_URL).open();

        String name = fixedLengthName(255);

        page.submitNewStudent(name, "Computer Science", "3.5");

        long id = page.waitForStudentNamed(name);

        assertEquals(255, page.nameOf(id).length());
        assertEquals(name, page.nameOf(id));
    }

    // TC-11: Student name exceeds maximum length
    @Test
    @DisplayName("TC-11: Student name of 256 characters is rejected")
    void studentName256CharactersIsRejected() {
        StudentListPage page =
                new StudentListPage(driver, wait, BASE_URL).open();

        String name = fixedLengthName(256);

        page.submitNewStudent(name, "Computer Science", "3.5");

        assertFalse(studentAppears(page, name),
                "A 256-character student name should not be created.");

        assertEquals(name, page.nameInputValue(),
                "The invalid name should remain available.");
    }

    // TC-12: Edit student name at maximum length
    @Test
    @DisplayName("TC-12: Student name can be edited to 255 characters")
    void editStudentName255Characters() {
        StudentListPage page =
                new StudentListPage(driver, wait, BASE_URL).open();

        String originalName = unique("Original Name");

        page.submitNewStudent(originalName, "Mathematics", "3.4");

        long id = page.waitForStudentNamed(originalName);

        String updatedName = fixedLengthName(255);

        page.editStudent(id, updatedName, "Mathematics", "3.4");

        assertEquals(updatedName, page.nameOf(id));
        assertEquals(255, page.nameOf(id).length());
        assertEquals("Mathematics", page.majorOf(id));
        assertGpaEquals("3.4", page.gpaOf(id));
    }

    // TC-13: Edit student name exceeds maximum length
    @Test
    @DisplayName("TC-13: Student name of 256 characters is rejected during edit")
    void editStudentName256CharactersIsRejected() {
        StudentListPage page =
                new StudentListPage(driver, wait, BASE_URL).open();

        String originalName = unique("Original Name");

        page.submitNewStudent(originalName, "Mathematics", "3.4");

        long id = page.waitForStudentNamed(originalName);

        page.openEdit(id);

        String invalidName = fixedLengthName(256);

        driver.findElement(By.id("edit-student-name")).clear();
        driver.findElement(By.id("edit-student-name")).sendKeys(invalidName);

        driver.findElement(By.id("edit-student-save-button")).click();

        assertEquals(originalName, page.nameOf(id),
                "The original name should remain unchanged.");

        assertEquals("Mathematics", page.majorOf(id));
        assertGpaEquals("3.4", page.gpaOf(id));
    }

    // TC-14: Cancel student edit (P2)
    @Test
    @DisplayName("TC-14: Canceling a student edit preserves original information")
    void cancelStudentEdit() {
        StudentListPage page =
                new StudentListPage(driver, wait, BASE_URL).open();

        String originalName = unique("Cancel Student");
        String originalMajor = "Mathematics";
        String originalGpa = "3.2";

        page.submitNewStudent(originalName, originalMajor, originalGpa);

        long id = page.waitForStudentNamed(originalName);

        String changedName = unique("Changed Student");

        page.cancelEdit(
                id,
                changedName,
                "Physics",
                "3.9"
        );

        assertEquals(originalName, page.nameOf(id),
                "Cancel should preserve the original student name.");

        assertEquals(originalMajor, page.majorOf(id),
                "Cancel should preserve the original major.");

        assertGpaEquals(originalGpa, page.gpaOf(id),
                "Cancel should preserve the original GPA.");

        assertFalse(studentAppears(page, changedName),
                "The changed name should not appear after canceling.");
    }

    // Helper: checks whether a student appears in the table
    private boolean studentAppears(StudentListPage page, String name) {
        return page.rows().stream()
                .anyMatch(row -> row.getText().contains(name));
    }

    // Helper: creates a unique name with an exact requested length
    private String fixedLengthName(int length) {
        String prefix = unique("S").replaceAll("[^a-zA-Z0-9]", "");

        return (prefix + "X".repeat(length))
                .substring(0, length);
    }

    // Helper: compares GPA values numerically
    private void assertGpaEquals(String expected, String actual) {
        assertGpaEquals(expected, actual, "GPA values should match.");
    }

    private void assertGpaEquals(
            String expected,
            String actual,
            String message) {

        assertEquals(
                Double.parseDouble(expected),
                Double.parseDouble(actual),
                0.0001,
                message
        );
    }
}
