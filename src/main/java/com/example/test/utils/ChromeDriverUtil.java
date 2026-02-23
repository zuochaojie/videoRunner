package com.example.test.utils;

import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;

public class ChromeDriverUtil {
    public static ChromeDriver getDriver(String url)
    {
        File file = new File("C:\\Program Files\\Google\\Chrome\\Application\\chromedriver.exe");
        System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());
        ChromeDriver driver = new ChromeDriver();
        driver.navigate().to(url);
        driver.manage().window().maximize();
        return driver;
    }
}
