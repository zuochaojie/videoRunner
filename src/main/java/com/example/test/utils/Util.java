package com.example.test.utils;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Util {
    private static final Map<String, String> map = new HashMap<>();
    private static final int CONNECT_TIMEOUT = 10000;
    private static final int SOCKET_TIMEOUT = 15000;
    private static final int CONNECTION_RESET_COUNT = 20;

    private static final Map<String, Boolean> status = new ConcurrentHashMap<>();

    public static boolean saveUrl2File(String urlStr, String fileName) {
        return saveUrl2File(urlStr, fileName, 1);
    }

    public static boolean saveUrl2File(String urlStr, String fileName, int count) {
        String substring = fileName.substring(0, fileName.lastIndexOf("\\"));
        File dir = new File(substring);
        dir.mkdirs();
        int cnt = 0;
        do {
            try {
                File file = new File(fileName);
                if (file.exists()) {
                    return true;
                }
                cnt++;
                FileUtils.copyURLToFile(new URL(urlStr), new File(fileName), CONNECT_TIMEOUT, SOCKET_TIMEOUT);
                File downloadFile = new File(fileName);
                if (fileName.endsWith("html") && downloadFile.length() < 512) {
                    downloadFile.delete();
                    continue;
                }
                if (file.length() == 0) {
                    file.delete();
                    continue;
                }
                System.out.println(urlStr+"下载成功");
                map.remove(urlStr);
                return true;
            } catch (IOException e) {
                new File(fileName).delete();
                if (e instanceof FileNotFoundException) {
                    status.put(urlStr, true);
                    return false;
                }
                String message = e.getMessage();
                if (!message.equals(map.get(urlStr))) {
                    map.put(urlStr, message);
                }
            }
        } while (cnt < count);
        return false;
    }

    public static boolean isNotFount(String url) {
        Boolean isNotFount = status.getOrDefault(url, false);
        if (isNotFount) {
            status.remove(url);
        }
        return isNotFount;
    }

    private static CloseableHttpClient createHttpClient() {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setSocketTimeout(20000)
                .build();
        HttpRequestRetryHandler retryHandler = new StandardHttpRequestRetryHandler(60, true);
        return HttpClients.custom().setRetryHandler(retryHandler).disableCookieManagement()
                .setDefaultRequestConfig(config)
                .build();
    }

    public static String postSearch(String keyWords, String url) {
        HttpPost http = new HttpPost(url);
        String agent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36";
        http.setHeader("Connection", "keep-alive");
        http.setHeader("User-Agent", agent);
        http.setHeader("Content-Type", "application/json");
        String jsonParam = "[{\"search\":\"keyWords\"},50,1]".replace("keyWords", keyWords);
        StringEntity entity = new StringEntity(jsonParam, "UTF-8");
        http.setEntity(entity);
        try (CloseableHttpClient httpClient = createHttpClient();
             CloseableHttpResponse response = httpClient.execute(http)) {
            HttpEntity responseEntity = response.getEntity();
            String result = EntityUtils.toString(responseEntity, "UTF-8");
            return result;
        } catch (Exception e) {
        }
        return "";
    }

    public static String getFileContent(String fileName) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
        }
        return sb.toString();
    }

    public static List<String> getLineList(String fileName) {
        List<String> list = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                list.add(line);
            }
        } catch (Exception e) {
        }
        return list;
    }

    public static void writeList2Txt(String filepath, List<String> list) {
        File file = new File(filepath);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, file.exists()));) {
            for (String s : list) {
                bw.write(s);
                bw.newLine();
            }
        } catch (IOException e) {

        }
    }
}
