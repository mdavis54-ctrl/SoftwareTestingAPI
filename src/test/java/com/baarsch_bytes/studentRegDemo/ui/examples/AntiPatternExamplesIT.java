package com.baarsch_bytes.studentRegDemo.ui.examples;

import com.baarsch_bytes.studentRegDemo.ui.UiTestBase;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests written the way UI tests often are written, including by AI assistants.
 * Each one has at least one problem. Your job in Part 1 is to find them, explain them,
 * and fix them.
 *
 * These are tagged "examples" and are skipped by `mvn verify`. To run them:
 *   mvn verify -Dui.excludedGroups= -Dit.test=AntiPatternExamplesIT
 */
@Tag("examples")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AntiPatternExamplesIT extends UiTestBase {

    @Test
    @Order(1)
    void createStudent() throws InterruptedException {
        driver.get(BASE_URL + "/students");
        Thread.sleep(1000);
        driver.findElement(By.id("new-student-name")).sendKeys("Grace Hopper");
        driver.findElement(By.id("new-student-major")).sendKeys("Computer Science");
        driver.findElement(By.id("new-student-gpa")).sendKeys("3.8");
        driver.findElement(By.id("add-student-button")).click();
        Thread.sleep(1000);
        List<WebElement> rows = driver.findElements(By.cssSelector("#student-list-table tbody tr"));
        assertEquals("Grace Hopper", rows.get(rows.size() - 1).findElement(By.tagName("td")).getText());
    }

    @Test
    @Order(2)
    void editStudent() throws InterruptedException {
        driver.get(BASE_URL + "/students");
        Thread.sleep(1000);
        driver.findElement(By.id("edit-student-button")).click();
        WebElement major = driver.findElement(By.id("edit-student-major"));
        major.clear();
        major.sendKeys("Mathematics");
        driver.findElement(By.id("edit-student-save-button")).click();
        assertEquals("Mathematics", major.getAttribute("value"));
    }

    @Test
    @Order(3)
    void studentTableHasData() {
        driver.get(BASE_URL + "/students");
        WebElement cell = driver.findElement(
                By.xpath("/html/body/div/div/div[2]/div/table/tbody/tr[1]/td[3]"));
        assertTrue(cell.isDisplayed());
    }

    @Test
    @Order(4)
    void deleteStudent() {
        try {
            driver.get(BASE_URL + "/students");
            driver.findElement(By.id("delete-student-button")).click();
        } catch (Exception e) {
            System.out.println("Delete test had a problem: " + e.getMessage());
        }
    }
}
