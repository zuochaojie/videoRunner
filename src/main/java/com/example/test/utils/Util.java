package com.example.test.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
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
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Util {
    private static final Map<String, String> map = new HashMap<>();
    private static final int CONNECT_TIMEOUT = 10000;
    private static final int SOCKET_TIMEOUT = 15000;
    private static final int CONNECTION_RESET_COUNT = 1000;
    private static final Map<String, Boolean> status = new ConcurrentHashMap<>();
    private static List<String> list = new CopyOnWriteArrayList<>();
    private static Thread thread = null;

    public static void addThunderTask(String url, String savePath) {
        String dir = "D:\\迅雷下载\\";
        File[] files = new File(dir).listFiles();
        String substring = url.substring(url.lastIndexOf("/") + 1);
        for (File file : files) {
            if (file.getName().equals(substring)) {
                try {
                    FileUtils.copyFile(file, new File(savePath));
                    file.delete();
                    return;
                } catch (IOException e) {

                }
            }
        }
        String thunderPath = "C:\\Program Files (x86)\\Thunder Network\\Thunder\\Program\\Thunder.exe";
        String[] command = {thunderPath, url, "/start", "/f:" + savePath, "/t:0"};
        ProcessBuilder pb = new ProcessBuilder(command);
        try {
            pb.start();
            File file = new File(dir + substring);
            if (list.isEmpty()) {
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (!list.isEmpty()) {
                            try {
                                Thread.sleep(1 * 1000);
                            } catch (InterruptedException e) {

                            }
                            for (String s : list) {
                                if (new File(s).exists()) {
                                    list.remove(s);
                                    try {
                                        FileUtils.copyFile(file, new File(savePath));
                                    } catch (IOException e) {
                                        System.out.println(e.getMessage());
                                    }
                                    file.delete();
                                }
                            }
                        }

                    }
                });
            }
            list.add(dir + substring);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static boolean saveUrl2File(String urlStr, String fileName) {
        return saveUrl2File(urlStr, fileName, CONNECTION_RESET_COUNT);
    }

    public static boolean saveUrl2File(String urlStr, String fileName, int count) {
        String substring = fileName.substring(0, fileName.lastIndexOf("\\"));
        File dir = new File(substring);
        File file = new File(fileName);
        dir.mkdirs();
        int cnt = 0;
        do {
            try {
                if (file.exists()) {
                    return true;
                }
                cnt++;
                FileUtils.copyURLToFile(new URL(urlStr), new File(fileName), CONNECT_TIMEOUT, SOCKET_TIMEOUT);
                File downloadFile = new File(fileName);
                if (downloadFile.length() < 512) {
                    downloadFile.delete();
                    continue;
                }
                System.out.println(urlStr + "下载成功");
                map.remove(urlStr);
                return true;
            } catch (IOException e) {
                file.delete();
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

    public static CloseableHttpClient createHttpClient() {
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

    public static void httpClientSaveUrl(String url, String referer, String cookies, String filePath) {
        if (new File(filePath).exists()) {
            return;
        }
        HttpGet httpGet = new HttpGet(url);
        String agent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36";
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("User-Agent", agent);
        httpGet.setHeader("Cookie", cookies);
        httpGet.setHeader("referer", referer);
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
             BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath));
        ) {
            HttpEntity httpEntity = httpResponse.getEntity();
            String content = EntityUtils.toString(httpEntity);
            bufferedWriter.write(content);
        } catch (IOException e) {
            new File(filePath).delete();
            System.out.println(e.getMessage());
        }
    }
}
