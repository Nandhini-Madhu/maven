package com.test;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;
import java.time.Duration;

@Listeners(stop.class)
public class test {

    WebDriver driver;
    WebDriverWait wait;

    @BeforeMethod
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get("https://maven-navy.vercel.app/");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));
    }

    @DataProvider(name = "formData")
    public Object[][] getData() {
        return new Object[][]{
                {"Nandhini", "9876543210", "CS2024001", true},
                {"", "9876543210", "CS2024001", false},
                {"", "", "CS2024001", false},
                {"Arjun Kumar", "123", "CS2024001", false},
                {"Arjun Kumar", "9876543210", "", false},
                {"", "", "", false}
        };
    }

    @Test(dataProvider = "formData")
    public void testRegisterButton(String name, String phone, String roll, boolean expectedState) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name"))).clear();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("phone"))).clear();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("roll"))).clear();

        driver.findElement(By.id("name")).sendKeys(name);
        driver.findElement(By.id("phone")).sendKeys(phone);
        driver.findElement(By.id("roll")).sendKeys(roll);

        ((JavascriptExecutor) driver).executeScript(
            "['name','phone','roll'].forEach(id => {" +
            "  var el = document.getElementById(id);" +
            "  el.dispatchEvent(new Event('input', {bubbles:true}));" +
            "  el.dispatchEvent(new Event('blur',  {bubbles:true}));" +
            "});"
        );

        try { Thread.sleep(600); } catch (InterruptedException e) {}

        WebElement button = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.id("registerBtn"))
        );

        boolean actualState = button.isEnabled();
        System.out.println("Expected: " + expectedState + " | Actual: " + actualState);
        Assert.assertEquals(actualState, expectedState, "Button state mismatch");
    }

    @AfterMethod
    public void teardown() {
        driver.quit();
    }
}
