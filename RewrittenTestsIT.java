
package com.baarsch_bytes.studentRegDemo.ui;

import com.baarsch_bytes.studentRegDemo.ui.pages.StudentListPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RewrittenTestsIT extends UiTestBase {

    @Test
    @DisplayName("A newly created student appears with the correct information")
    void createStudent() {
        StudentListPage page =
                new StudentListPage(driver, wait, BASE_URL).open();

        String name = unique("Grace Hopper");

        page.submitNewStudent(
                name,
                "Computer Science",
                "3.8"
        );

        long id = page.waitForStudentNamed(name);

        assertEquals(name, page.nameOf(id));
        assertEquals("Computer Science", page.majorOf(id));
        assertEquals("3.8", page.gpaOf(id));
    }

    @Test
    @DisplayName("The student table displays a newly created student")
    void studentTableHasData() {
        StudentListPage page =
                new StudentListPage(driver, wait, BASE_URL).open();

        String name = unique("Table Test Student");

        page.submitNewStudent(
                name,
                "Mathematics",
                "3.5"
        );

        long id = page.waitForStudentNamed(name);

        assertTrue(
                page.rows().stream()
                        .anyMatch(row -> row.getText().contains(name)),
                "The newly created student should appear in the table"
        );

        assertEquals(name, page.nameOf(id));
    }
}
