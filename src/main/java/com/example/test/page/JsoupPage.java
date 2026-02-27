package com.example.test.page;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsoupPage {
    private Document doc;
    private String fileName;
    private static Set<String> set = new HashSet<>();
    private Pattern pattern = Pattern.compile("[a-fA-F0-9]{40}");
    public boolean dateBreak = false;

    public JsoupPage(String file) {
        File file1 = new File(file);
        try {
            doc = Jsoup.parse(new File(file));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        fileName = file1.getName();
    }

    /**
     * title,detailSrc
     *
     * @return
     */
    public List<String[]> getMovieTitleList() {
        List<String[]> list = new ArrayList<>();
        Elements elements = doc.select(".item");
        for (Element element : elements) {
            String[] array = new String[3];
            Element photoInfo = element.select(".photo-info").get(0);
            Elements dates = photoInfo.select("date");
            String src = element.select("a").get(0).attr("href");
            array[0] = dates.get(0).text();
            array[1] = "http:" + src;
            array[2] = fileName.substring(0, fileName.lastIndexOf("_"));
            list.add(array);
        }
        return list;
    }

    public boolean hasJp(String text) {
        // 检查是否包含日语特有字符
//        for (char c : text.toCharArray()) {
//            // 平假名范围
//            if (c >= '\u3040' && c <= '\u309F') {
//                return true;
//            }
//            // 片假名范围
//            if (c >= '\u30A0' && c <= '\u30FF') {
//                return true;
//            }
//            // 半角片假名
//            if (c >= '\uFF65' && c <= '\uFF9F') {
//                return true;
//            }
//        }
        return true;
    }

    public boolean uncensored(String text) {
        String[] array = {"heyzo", "pon", "paco", "cari", "musume"};
        String lowerCase = text.toLowerCase(Locale.ROOT);
        for (String s : array) {
            if (lowerCase.contains(s)) {
                return true;
            }
        }
        return false;
    }


    public String getMagnetText() {
        Elements select = doc.select(".magnet-text");
        if (select.isEmpty()) {
            try {
                String text = doc.select(".tpc_content").get(0).text();
                if (text.contains("rmdown")) {
                    text = text.replace("261", "");
                }
                Matcher matcher = pattern.matcher(text);
                String string = "";
                while (matcher.find()) {
                    string = matcher.group();
                }
                if (set.contains(string)) {
                    return "";
                }
                set.add(string);
                return string;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return "";
        }
        String text = select.get(0).text();
        String substring = text.substring(text.lastIndexOf(":") + 1);
        if (set.contains(substring)) {
            return "";
        }
        set.add(substring);
        return substring;
    }

    public int getDuration() {
        String text = doc.select(".tpc_content").get(0).text();
        char[] charArray = text.toCharArray();
        for (int index = 0; index < charArray.length; index++) {
            if (charArray[index] == ':' && charArray[index + 3] == ':') {
                StringBuilder builder = new StringBuilder();
                for (int i = index - 2; i <= index + 5; i++) {
                    builder.append(charArray[i]);
                }
                return convertToSeconds(builder.toString());
            }
        }
        return 0;
    }

    public List<String> getAllPageUrl(Collection<String> collection, String start) {
        List<String> list = new ArrayList<>();
        Elements elements = doc.select("#ajaxtable");
        Elements trList = elements.get(0).child(1).children();
        for (int i = 6; i < trList.size() - 2; i++) {
            Element element = trList.get(i).select("a").get(1);
            String text = element.text();
            if (text.contains("heyzo")) {
                text.replace("heyzo", "HEYZO");
            }
            text = text.replace("HEYZO ", "HEYZO-");
            String date = trList.get(i).child(4).select("span").text();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date parsedate = format.parse(date);
                Date startdate = format.parse(start);
                if (parsedate.before(startdate)) {
                    dateBreak=true;
                    break;
                }
            } catch (ParseException e) {
                System.out.println(e.getMessage());
            }
            if (!uncensored(text)) {
                continue;
            }
            for (String s : collection) {
                if (text.contains(s)) {
                    list.add(s + "_" + element.select("a").get(0).attr("href"));
                    break;
                }
            }
        }
        return list;
    }

    private static int convertToSeconds(String timeStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            LocalTime time = LocalTime.parse(timeStr, formatter);
            return time.toSecondOfDay();
        } catch (Exception e) {
            System.out.println(timeStr);
        }
        return 0;
    }

    public List<String> getActressImage() {
        List<String> list = new ArrayList<>();
        Elements frame = doc.select(".photo-frame");
        Elements info = doc.select(".photo-info");
        for (int i = 0; i < frame.size(); i++) {
            Element element = frame.get(i);
            String src = element.children().get(0).attr("src");
            String text = info.get(i).text();
            list.add(text + "_" + src);
        }
        return list;
    }
}
