package com.test;

import com.aventstack.extentreports.*;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class stop implements ITestListener {

    ExtentReports extentReports = extent.getReportInstance();  // ✅ call static method
    ExtentTest test;

    @Override
    public void onTestStart(ITestResult result) {
        test = extentReports.createTest(result.getName());     // ✅ ExtentReports method
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.pass("Test Passed ✅");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        test.fail("Test Failed ❌");
        test.fail(result.getThrowable());
        extentReports.flush();                                
    }

    @Override
    public void onFinish(org.testng.ITestContext context) {
        extentReports.flush();                                
    }
}