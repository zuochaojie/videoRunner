package com.example.test.page;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class HtmlPage implements AutoCloseable {

    protected WebDriver webDriver;

    public HtmlPage() {

    }

    public void sendKeys(String css, String value) {
        getElementBycss(css).sendKeys(value);
        waitSleep(1);
    }

    public void setOptions() {
        Select select = new Select(getElementBycss(".form-select"));
        select.selectByValue("4");
    }

    public void waitElementBycss(int second, String css) {
        List<WebElement> list;
        do {
            waitSleep(1);
            second--;
            list = getElementsBycss(css);
        } while (list.isEmpty() && second >= 0);
    }

    public void click(By by) {
        webDriver.findElement(by).click();
        waitSleep(2);
    }

    public WebElement getElementBycss(String css) {
        return webDriver.findElement(By.cssSelector(css));
    }

    public List<WebElement> getElementsBycss(String css) {
        return webDriver.findElements(By.cssSelector(css));
    }

    public WebElement getElementId(String strId) {
        return webDriver.findElement(By.id(strId));
    }

    public WebElement getElementBycss(WebElement element, String css) {
        try {
            return element.findElement(By.cssSelector(css));
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void waitSleep(int second) {
        try {
            Thread.sleep(second * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void scrollToElement(WebElement element) {
        JavascriptExecutor executor = (JavascriptExecutor) webDriver;
        executor.executeScript("arguments[0].scrollIntoView();", element);
    }

    public void waitPageLoad(int second) {
        JavascriptExecutor js = (JavascriptExecutor) webDriver;
        String complete = "complete";
        String status = "";
        do {
            status = js.executeScript("return document.readyState").toString();
            waitSleep(1);
            second--;
        } while (!status.equals(complete) && second > 0);
    }

    public void nextPage() {
        String currentUrl = webDriver.getCurrentUrl();
        int i = currentUrl.lastIndexOf("=");
        String urlPre = currentUrl.substring(0, i + 1);
        String currentPage = currentUrl.substring(i + 1);
        int nextPageNum = Integer.parseInt(currentPage) + 1;
        String nextPage = urlPre + nextPageNum;
        webDriver.navigate().to(nextPage);
        waitSleep(2);
    }

    public void navigate(String url) {
        webDriver.navigate().to(url);
        waitSleep(2);
    }

    @Override
    public void close() throws Exception {
        if (webDriver == null) {
            return;
        }
        webDriver.close();
        webDriver.quit();
    }
}