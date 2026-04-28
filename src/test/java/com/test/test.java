package com.test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.*;

@Listeners(stop.class)
public class test {

    WebDriver driver;

    @BeforeMethod
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();

       driver.get("https://firstform-mauve.vercel.app");
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
    public void testRegisterButton(String name, String phone, String roll, boolean expectedState) throws InterruptedException {

        driver.findElement(By.id("name")).clear();
        driver.findElement(By.id("phone")).clear();
        driver.findElement(By.id("roll")).clear();

        driver.findElement(By.id("name")).sendKeys(name);
        driver.findElement(By.id("phone")).sendKeys(phone);
        driver.findElement(By.id("roll")).sendKeys(roll);

        WebElement button = driver.findElement(By.id("registerBtn"));

        Thread.sleep(1000);

        boolean actualState = button.isEnabled();

        System.out.println("Expected: " + expectedState + " | Actual: " + actualState);

        Assert.assertEquals(actualState, expectedState,
                "Button state mismatch");
    }

    @AfterMethod
    public void teardown() {
        driver.quit();
    }
}