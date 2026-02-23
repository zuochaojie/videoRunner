package com.example.test.page;

import com.example.test.model.VideoModel;
import com.example.test.utils.ChromeDriverUtil;
import com.example.test.utils.Util;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;

import java.io.File;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RenRenHtmlPage extends HtmlPage implements AutoCloseable {

    public static Map<String, String> map = new ConcurrentHashMap<>();

    private String baseUrl;

    public RenRenHtmlPage() {

    }

    public void initPage() {
        super.webDriver = ChromeDriverUtil.getDriver("http://2048.lc");
        waitSleep(2);
        String href = getElementsBycss(".link").get(1).getAttribute("href");
        baseUrl = href;
        webDriver.navigate().to(href + "thread.php?fid=4");
        waitSleep(2);
        click(By.cssSelector(".primary"));
        webDriver.navigate().to(href + "thread.php?fid=4");
        waitSleep(2);
    }

    public Map<String, List<String>> search(List<VideoModel> videoModelList, String start) {
        String text = getElementsBycss(".pagesone span").get(0).getText();
        String substring = text.substring(text.indexOf("/") + 1);
        int total = Integer.parseInt(substring);
        Set<Cookie> cookies = webDriver.manage().getCookies();
        StringBuilder builder = new StringBuilder();
        Iterator<Cookie> iterator = cookies.iterator();
        while (iterator.hasNext()) {
            Cookie cookie = iterator.next();
            builder.append(cookie.getName()).append("=").append(cookie.getValue());
            if (iterator.hasNext()) {
                builder.append("; ");
            }
        }
        String downLoadDir = "D:\\document\\static\\html\\renren\\";
        String urlPre = baseUrl + "thread.php?fid=4&page=";
        String cookie = builder.toString();
        builder.setLength(0);
        StringBuilder stringBuilder = new StringBuilder(urlPre);
        Set<String> list = videoModelList.stream().map(s -> s.getTitle()).collect(Collectors.toSet());
        int length = stringBuilder.length();
        Map<String, List<String>> map = new HashMap<>();
        try {
            int count = 1;
            for (int i = total; i >=1; i--) {
                stringBuilder.append(count);
                String filePath = downLoadDir + i + ".html";
                Util.httpClientSaveUrl(stringBuilder.toString(), "", cookie, filePath);
                count++;
                stringBuilder.setLength(length);
            }
            for (int i = total; i >=1; i--) {
                String filePath = downLoadDir + i + ".html";
                JsoupPage jsoupPage = new JsoupPage(filePath);
                List<String> pageUrl = jsoupPage.getAllPageUrl(list,start);
                for (String s : pageUrl) {
                    int index = s.lastIndexOf("_");
                    String movieTitle = s.substring(0, index);
                    String temp = downLoadDir + movieTitle + ".html";
                    String url = baseUrl + s.substring(index + 1);
                    Util.httpClientSaveUrl(url, "", cookie, temp);
                    JsoupPage detail = new JsoupPage(temp);
                    new File(temp).delete();
                    String magnetText = detail.getMagnetText();
                    if (magnetText.isEmpty()) {
                        continue;
                    }
                    String movieUUid = videoModelList.stream().filter(e -> e.getTitle().equals(movieTitle)).map(e -> e.getId()).toArray(String[]::new)[0];
                    List<String> orDefault = map.getOrDefault(movieUUid, new ArrayList<>());
                    orDefault.add(magnetText);
                    map.put(movieUUid, orDefault);
                }
                if (jsoupPage.dateBreak){
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return map;
    }

    public void back() {
        super.webDriver.navigate().back();
    }


    public boolean hasJp(String text) {
        // 检查是否包含日语特有字符
        for (char c : text.toCharArray()) {
            // 平假名范围
            if (c >= '\u3040' && c <= '\u309F') {
                return true;
            }
            // 片假名范围
            if (c >= '\u30A0' && c <= '\u30FF') {
                return true;
            }
            // 半角片假名
            if (c >= '\uFF65' && c <= '\uFF9F') {
                return true;
            }
        }
        return false;
    }


    private void clickNextPage() {
        String currentUrl = webDriver.getCurrentUrl();
        int index = currentUrl.lastIndexOf("=") + 1;
        String currentPage = currentUrl.substring(index);
        int nextPage = Integer.parseInt(currentPage) + 1;
        String nextHref = currentUrl.substring(0, index) + nextPage;
        webDriver.navigate().to(nextHref);
        waitSleep(1);
    }

    @Override
    public void close() throws Exception {
        webDriver.close();
        webDriver.quit();
    }
}
