package com.baarsch_bytes.studentRegDemo.ui;

import com.baarsch_bytes.studentRegDemo.ui.pages.StudentListPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** An example of a UI test that is independent, waits properly, and checks the outcome. */
class StudentListSmokeIT extends UiTestBase {

    @Test
    @DisplayName("A new student appears in the table with the values entered")
    void addedStudentAppearsInTable() {
        StudentListPage page = new StudentListPage(driver, wait, BASE_URL).open();
        String name = unique("Ada Lovelace");

        page.submitNewStudent(name, "Mathematics", "3.9");
        long id = page.waitForStudentNamed(name);

        assertEquals(name, page.nameOf(id));
        assertEquals("Mathematics", page.majorOf(id));
        assertEquals("3.9", page.gpaOf(id));
    }
}
