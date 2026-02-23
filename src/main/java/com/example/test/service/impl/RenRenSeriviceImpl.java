package com.example.test.service.impl;

import com.example.test.service.RenRenSerivce;
import com.example.test.utils.Util;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class RenRenSeriviceImpl implements RenRenSerivce {
    @Override
    public void getCookie() {

        String url = "https://2048.lc";
        try (CloseableHttpClient httpClient = Util.createHttpClient()) {
            Document parse = Jsoup.parse(new URL(url), 10 * 1000);
            String href = parse.select(".link").get(1).attr("href");
            CloseableHttpResponse response = httpClient.execute(new HttpGet(href));
            String tok  = response.getHeaders("set-cookie")[0].getValue();
            response.close();
            HttpPost post = new HttpPost(href);

        } catch (IOException e) {
        }
    }

    @Override
    public Map<String, List<String>> getJson(Collection<String> list) {
        return Map.of();
    }
}
