package com.example.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.test.mapper.CompanyMapper;
import com.example.test.message.RocketMQProducer;
import com.example.test.model.CompanyModel;
import com.example.test.model.ImageModel;
import com.example.test.model.VideoModel;
import com.example.test.service.ActressService;
import com.example.test.service.CompanyService;
import com.example.test.service.ImageService;
import com.example.test.service.VideoService;
import com.example.test.utils.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.uuid.UuidCreator;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service("companyService")
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, CompanyModel> implements CompanyService {
    @Autowired
    private VideoService videoService;
    @Autowired
    private ActressService actressService;
    @Autowired
    private RocketMQProducer rocketMQProducer;

    private ObjectMapper objectMapper = new ObjectMapper();

    private final AtomicInteger completeCount = new AtomicInteger(0);
    @Autowired
    private ImageService imageService;
    @Value("${filepath.imgdir}")
    public String IMG_DIR;
    @Value("${filepath.tempdownload}")
    public String TEMP_DOWNLOAD_DIR;
    public static ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 4, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

    @Override
    @Transactional(rollbackFor = {Throwable.class})
    public boolean save(CompanyModel model) {
        baseMapper.insert(model);
        return true;
    }

    @Transactional(rollbackFor = {Throwable.class})
    public boolean saveBatch(Collection<CompanyModel> list) {
        baseMapper.insert(list);
        return true;
    }

    @Override
    @Transactional(rollbackFor = {Throwable.class})
    public boolean removeById(Serializable id) {
        baseMapper.deleteById(id);
        return true;
    }

    @Override
    @Transactional(rollbackFor = {Throwable.class})
    public boolean removeBatchByIds(Collection<?> ids) {
        baseMapper.deleteByIds(ids);
        return true;
    }

    @Override
    @Transactional(rollbackFor = {Throwable.class})
    public boolean updateById(CompanyModel model) {
        baseMapper.updateById(model);
        return true;
    }

    @Override
    @Transactional
    public CompanyModel getById(Serializable id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional
    public Map<String, Object> queryPage(Map<String, Object> params) {
        int pageSize = Integer.parseInt(params.getOrDefault("pageSize", "10").toString());
        int pageNum = Integer.parseInt(params.getOrDefault("pageNum", 1).toString());
        Page<Object> objects = PageHelper.startPage(pageNum, pageSize);
        params.clear();
        PageInfo pageInfo = new PageInfo(baseMapper.selectByMap(params));
        getNewMovie();
        params.put("data", pageInfo.getList());
        return params;
    }

    private static Queue<String> getFileNameList() {
        Queue<String> list = new LinkedList<>();
        for (int i = 1; i <= 200; i++) {
            if (i < 10) {
                list.add("00" + i + ".jpg");
            } else if (i < 100) {
                list.add("0" + i + ".jpg");
            } else {
                list.add(i + ".jpg");
            }
        }

        return list;
    }

    //    @Scheduled(cron = "0 0 0/1 * * ? ")
    @Override
    public void getNewMovie() {
        completeCount.set(0);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                caribeanTask();
                completeCount.getAndIncrement();
            }
        });
        executor.execute(new Runnable() {
            @Override
            public void run() {
                pacopacomamaTask();
                completeCount.getAndIncrement();
            }
        });
        executor.execute(new Runnable() {
            @Override
            public void run() {
                pondoTask();
                completeCount.getAndIncrement();
            }
        });
        executor.execute(new Runnable() {
            @Override
            public void run() {
                heyzoTask();
                completeCount.getAndIncrement();
            }
        });
        executor.execute(new Runnable() {
            @Override
            public void run() {
                musume();
                completeCount.getAndIncrement();
            }
        });
//        updateAddress();
    }

    private void updateAddress() {
        String jsonFile = "D:\\下载\\renren.json";
        try {
            Map<String, List<String>> map = objectMapper.readValue(new File(jsonFile), Map.class);
            VideoModel model = new VideoModel();
            Set<String> strings = map.keySet();
            for (String string : strings) {
                String[] array = map.get(string).stream().toArray(String[]::new);
                model.setId(string);
                model.setAddress(array);
                videoService.updateById(model);
            }
        } catch (IOException e) {

        }
    }

    @Override
    public String getMovieTaskProcess() {
        BigDecimal total = new BigDecimal(5);
        BigDecimal complete = new BigDecimal(completeCount.get());
        return complete.divide(total, 1, RoundingMode.HALF_UP).toString() + "";
    }

    private void pacopacomamaTask() {
        CompanyModel companyModel = getById("pacopacomama");
        this.getJsonMovie(companyModel);
    }

    private void pondoTask() {
        CompanyModel companyModel = getById("1pondo");
        this.getJsonMovie(companyModel);
    }

    private void musume() {
        CompanyModel companyModel = getById("10musume");
        this.getJsonMovie(companyModel);
    }

    private void avsoxHeyzoTask() {
        String str = "http://avsox.click/ja/studio/74a9d0e356f0b5b8";
        String tempHtml = TEMP_DOWNLOAD_DIR + "heyzo_avsox.html";
        boolean success = Util.saveUrl2File(str, tempHtml, 10000);
        if (!success) {
            return;
        }
        File file = new File(tempHtml);
        try {
            Document parse = Jsoup.parse(file);
            file.delete();
            Elements elements = parse.select(".photo-info");
            for (Element element : elements) {
                Elements dates = element.select("date");
                String title = dates.get(0).text().trim();
                VideoModel videoModel = new VideoModel();
                videoModel.setTitle(title);
                videoModel.setProducedBy("HEYZO");
                VideoModel model = videoService.getBaseMapper().selectOne(Wrappers.query(videoModel));
                if (model != null) {
                    return;
                }
                String dateStr = dates.get(1).text().trim();
                try {
                    videoModel.setReleaseDate(Date.valueOf(dateStr));
                } catch (Exception e) {

                }
                String href = element.parent().attr("href");
                String movieInfo = "https:" + href;
                String movieHtml = TEMP_DOWNLOAD_DIR + "heyzo-" + title + ".html";
                Util.saveUrl2File(movieInfo, movieHtml, 10000);
                File file1 = new File(movieHtml);
                Document document = Jsoup.parse(file1);
                file1.delete();
                String[] strings = document.select(".genre").stream().map(Element::text).toArray(String[]::new);
                String actress = document.select(".avatar-box").get(0).text();
                videoModel.setTags(strings);
                videoModel.setActress(actress);
                videoModel.setId(UuidCreator.getTimeOrdered().toString());
                String releaseName = document.select("h3").get(0).text();
                if (releaseName.contains("】")) {
                    releaseName = releaseName.substring(releaseName.indexOf("】") + 1).trim();
                }
                releaseName = releaseName.replace(title, "").replace("～ - 無修正アダルト動画 HEYZO", "").replace("- 無修正アダルト動画 HEYZO", "").trim();
                videoModel.setReleaseName(releaseName);
                videoService.save(videoModel);
                List<String> heyzoImgUrl = getHeyzoImgUrl(title);
                ImageModel imageModel = new ImageModel();
                imageModel.setId(document.select(".bigImage").get(0).attr("href"));
                imageModel.setFilePath(IMG_DIR + "HEYZO\\" + videoModel.getTitle() + "\\" + videoModel.getTitle() + ".jpg");
                if (imageService.getById(imageModel.getId()) == null) {
                    imageService.save(imageModel);
                }
                Queue<String> fileNameList = getFileNameList();
                for (String s : heyzoImgUrl) {
                    imageModel.setId(s);
                    imageModel.setFilePath(IMG_DIR + "HEYZO\\" + videoModel.getTitle() + "\\" + fileNameList.poll());
                    if (imageService.getById(imageModel.getId()) == null) {
                        imageService.save(imageModel);
                    }
                }
            }
        } catch (IOException e) {

        }
    }

    private void avsox1PondoTask() {
        String str = "http://avsox.click/ja/studio/6aa5c06bb3d805f2";
        String tempHtml = TEMP_DOWNLOAD_DIR + "pondo_avsox.html";
        boolean success = Util.saveUrl2File(str, tempHtml, 10000);
        if (!success) {
            return;
        }
        try {
            File file = new File(tempHtml);
            Document parse = Jsoup.parse(file);
            file.delete();
            Elements elements = parse.select(".photo-info");
            for (Element element : elements) {
                Elements dates = element.select("date");
                String title = dates.get(0).text().trim();
                VideoModel videoModel = new VideoModel();
                videoModel.setTitle(title);
                videoModel.setProducedBy("1pondo");
                VideoModel model = videoService.getBaseMapper().selectOne(Wrappers.query(videoModel));
                if (model != null) {
                    return;
                }
                String dateStr = dates.get(1).text().trim();
                try {
                    videoModel.setReleaseDate(Date.valueOf(dateStr));
                } catch (Exception e) {
                    System.out.println(dateStr);
                }
                String href = element.parent().attr("href");
                String movieInfo = "https:" + href;
                String movieHtml = TEMP_DOWNLOAD_DIR + "1pondo-" + title + ".html";
                Util.saveUrl2File(movieInfo, movieHtml, 10000);
                File file1 = new File(movieHtml);
                Document document = Jsoup.parse(file1);
                file1.delete();
                String releaseName = document.select("h3").get(0).text().replace(title, "").trim();
                String[] strings = document.select(".genre").stream().map(Element::text).toArray(String[]::new);
                String actress = document.select(".avatar-box").get(0).text();
                videoModel.setTags(strings);
                videoModel.setReleaseName(releaseName);
                videoModel.setActress(actress);
                videoModel.setId(UuidCreator.getTimeOrdered().toString());
                videoService.save(videoModel);
                String tempJson = TEMP_DOWNLOAD_DIR + "1pondo" + title + ".json";
                boolean b = Util.saveUrl2File("https://www.1pondo.tv/dyn/dla/json/movie_gallery/" + title + ".json", tempJson, 10000);
                if (b) {
                    List<String> images = getMovieImgUrlsFromJson(tempJson);
                    ImageModel imageModel = new ImageModel();
                    imageModel.setId(document.select(".bigImage").get(0).attr("href"));
                    imageModel.setFilePath(IMG_DIR + "1pondo\\" + videoModel.getTitle() + "\\" + videoModel.getTitle() + ".jpg");
                    if (imageService.getById(imageModel.getId()) == null) {
                        imageService.save(imageModel);
                    }
                    Queue<String> fileNameList = getFileNameList();
                    for (String s : images) {
                        imageModel.setId("https://www.1pondo.tv/" + s);
                        imageModel.setFilePath(IMG_DIR + "1pondo\\" + videoModel.getTitle() + "\\" + fileNameList.poll());
                        if (imageService.getById(imageModel.getId()) == null) {
                            imageService.save(imageModel);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void heyzoTask() {
        CompanyModel companyModel = getById("HEYZO");
        String tempFile = TEMP_DOWNLOAD_DIR + companyModel.getCode() + ".html";
        boolean success = Util.saveUrl2File(companyModel.getNewMoviePage(), tempFile, 10000);
        if (!success) {
            return;
        }
        File htmlFile = new File(tempFile);
        Document document = null;
        try {
            document = Jsoup.parse(htmlFile);
            htmlFile.delete();
            Element entry = document.select(".movie-list").get(0);
            Elements children = entry.children();
            for (Element child : children) {
                String movieId = child.attr("data-movie-id");
                VideoModel videoModel = new VideoModel();
                videoModel.setTitle("HEYZO-" + movieId);
                videoModel.setProducedBy(companyModel.getCode().toUpperCase(Locale.ROOT));
                String releaseName = child.select(".lazy").attr("title");
                VideoModel model = videoService.getBaseMapper().selectOne(Wrappers.query(videoModel));
                if (model != null) {
                    continue;
                }
                String actress = child.select(".actor").get(0).text();
                String text = child.select(".release").get(0).text();
                String releaseDate = "";
                if (text.contains("公開期間:")) {
                    releaseDate = text.replace("公開期間:", "").trim().substring(0, 10);
                } else {
                    releaseDate = text.replace("公開日:", "").trim();
                }

                videoModel.setActress(actress);
                try {
                    videoModel.setReleaseDate(Date.valueOf(releaseDate));
                } catch (Exception e) {
                    System.out.println(releaseDate);
                }
                videoModel.setId(UuidCreator.getTimeOrdered().toString());
                videoModel.setReleaseName(releaseName);
                String imgUrl = companyModel.getMovieImage().replace("xxxx", movieId);
                String downLoading = IMG_DIR + companyModel.getCode() + "\\" + videoModel.getTitle() + "\\" + videoModel.getTitle() + ".jpg";
                saveDownload(imgUrl, downLoading);
                String movieDetails = companyModel.getMovieDetails();
                String detailUrl = movieDetails.replace("xxxx", movieId);
                String tempHtml = TEMP_DOWNLOAD_DIR + "HEYZO-" + movieId + ".html";
                boolean b = Util.saveUrl2File(detailUrl, tempHtml);
                if (!b) {
                    saveDownload(detailUrl, tempHtml);
                    continue;
                }
                Document parse = null;
                try {
                    File file = new File(tempHtml);
                    parse = Jsoup.parse(file);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                Elements elements = parse.select("#section_gallery");
                if (!elements.isEmpty()) {
                    List<String> heyImgUrl = getHeyzoImgUrl(movieId);
                    for (String s : heyImgUrl) {
                        downLoading = IMG_DIR + companyModel.getCode() + "\\" + videoModel.getTitle() + "\\" + s.substring(s.length() - 7);
                        saveDownload(s, downLoading);
                    }
                }
                Elements select = parse.select(".tag-keyword-list");
                String[] tags = new String[0];
                if (!select.isEmpty()) {
                    tags = select.get(0).children().stream().map(Element::text).toArray(String[]::new);
                }
                Boolean needUpdate = false;
                for (int k = 0; k < tags.length; k++) {
                    String tag = tags[k];
                    char[] charArray = tag.toCharArray();
                    for (int i = 0; i < charArray.length; i++) {
                        int code = charArray[i];
                        if (0xFF00 <= code && code <= 0xFFEF) {
                            needUpdate = true;
                            charArray[i] -= 0xFEE0;
                        }
                    }
                    if (needUpdate) {
                        tags[k] = new String(charArray);
                    }
                }
                videoModel.setTags(tags);
                videoService.save(videoModel);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void caribeanTask() {
        CompanyModel companyModel = getById("caribbeancom");
        String newMoviePage = companyModel.getNewMoviePage();
        String tempFile = TEMP_DOWNLOAD_DIR + companyModel.getCode() + ".html";
        boolean success = Util.saveUrl2File(newMoviePage, tempFile);
        if (!success) {
            saveDownload(newMoviePage, tempFile);
            return;
        }
        File htmlFlie = new File(tempFile);
        try {
            Document document = Jsoup.parse(htmlFlie);
            htmlFlie.delete();
            Elements elements = document.select(".media-image");
            for (Element entry : elements) {
                String href = entry.attr("data-video-src");
                String releaseName = entry.attr("title");
                String title = href.substring(46, href.lastIndexOf("/"));
                VideoModel videoModel = new VideoModel();
                videoModel.setTitle(title);
                videoModel.setProducedBy(companyModel.getCode());
                VideoModel model = videoService.getBaseMapper().selectOne(Wrappers.query(videoModel));
                if (model != null) {
                    return;
                }
                String movieDetails = companyModel.getMovieDetails();
                String detailUrl = movieDetails.replace("xxxx", title);
                String tempHtml = TEMP_DOWNLOAD_DIR + "caribbeancom-" + title + ".html";
                String imgUrl = companyModel.getMovieImage().replace("xxxx", title);
                String downLoadimg = IMG_DIR + companyModel.getCode() + File.separator + title + File.separator + title + ".jpg";
                saveDownload(imgUrl, downLoadimg);
                boolean b = Util.saveUrl2File(detailUrl, tempHtml);
                if (!b) {
                    saveDownload(detailUrl, tempHtml);
                    continue;
                }
                Document parse = null;
                File file = new File(tempHtml);
                try {
                    parse = Jsoup.parse(file);
                } catch (IOException e) {

                }
                Elements images = parse.select(".gallery-image");
                for (Element image : images) {
                    Element parent = image.parent();
                    String sample = parent.attr("data-is_sample");
                    String url = "";
                    if ("1".equals(sample)) {
                        url = parent.attr("href").substring(1);
                    } else {
                        url = image.attr("src").substring(1);
                    }
                    imgUrl = companyModel.getUrl() + url;
                    String substring = url.substring(url.lastIndexOf("/") + 1);
                    String viewImg = IMG_DIR + companyModel.getCode() + "\\" + title + "\\" + substring;
                    if (getById(imgUrl) != null) {
                        continue;
                    }
                    saveDownload(imgUrl, viewImg);
                }
                Elements select = parse.select(".spec-content");
                String actress = select.get(0).text();
                String text = select.get(1).text().replace("/", "-");
                try {
                    videoModel.setReleaseDate(Date.valueOf(text));
                } catch (Exception e) {
                }
                videoModel.setReleaseName(releaseName);
                videoModel.setId(UuidCreator.getTimeOrdered().toString());
                String[] strings = parse.select(".spec-item").stream().map(Element::text).toArray(String[]::new);
                videoModel.setTags(strings);
                videoModel.setActress(actress);
                videoService.save(videoModel);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private List<String> getHeyzoImgUrl(String movieId) {
        List<String> list = new ArrayList<>(22);
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/gallery/001.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/gallery/002.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/gallery/003.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/gallery/004.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/gallery/005.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/gallery/thumbnail_006.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/gallery/thumbnail_007.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/gallery/thumbnail_008.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/gallery/thumbnail_009.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/gallery/thumbnail_010.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/gallery/thumbnail_011.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/gallery/thumbnail_012.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/gallery/thumbnail_013.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/gallery/thumbnail_014.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/gallery/thumbnail_015.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/gallery/thumbnail_016.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/gallery/thumbnail_017.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/gallery/thumbnail_018.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/gallery/thumbnail_019.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/gallery/thumbnail_020.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/gallery/thumbnail_021.jpg");
        return list;
    }

    private void getJsonMovie(CompanyModel companyModel) {
        String fileName = TEMP_DOWNLOAD_DIR + companyModel.getCode() + ".json";
        boolean success = Util.saveUrl2File(companyModel.getNewMoviePage(), fileName, 10000);
        if (!success) {
            return;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> map = objectMapper.readValue(new File(fileName), Map.class);
            List<Map<String, Object>> rows = (List<Map<String, Object>>) map.get("Rows");
            for (Map<String, Object> row : rows) {

                VideoModel videoModel = new VideoModel();
                String movieID = row.get("MovieID").toString();
                videoModel.setProducedBy(companyModel.getCode());
                videoModel.setTitle(movieID);
                QueryWrapper<VideoModel> query = Wrappers.query(videoModel);
                VideoModel model = videoService.getBaseMapper().selectOne(query);
                String actor = row.get("Actor").toString();
                String release = row.get("Release").toString();
                String releaseName = row.get("Title").toString();
                List<String> list = (List<String>) row.get("UCNAME");
                String[] tags = new String[list.size()];
                list.toArray(tags);
                String imgUrl = row.get("ThumbHigh").toString();
                String imgFile = IMG_DIR + companyModel.getCode() + "\\" + movieID + "\\" + movieID + ".jpg";
                saveDownload(imgUrl, imgFile);
                videoModel.setReleaseDate(Date.valueOf(release));
                videoModel.setActress(actor);
                videoModel.setTags(tags);
                videoModel.setReleaseName(releaseName);
                videoModel.setId(UuidCreator.getTimeOrdered().toString());
                String temptation = TEMP_DOWNLOAD_DIR + companyModel.getCode() + "-" + movieID + ".json";
                String imageJson = companyModel.getUrl() + "dyn/dla/json/movie_gallery/" + movieID + ".json";
                boolean b = Util.saveUrl2File(imageJson, temptation);
                if (!b) {
                    saveDownload(imageJson, temptation);
                    continue;
                } else {
                    Queue<String> fileNameList = getFileNameList();
                    List<String> imageUrlList = getMovieImgUrlsFromJson(temptation);
                    for (String s : imageUrlList) {
                        String filePath = IMG_DIR + companyModel.getCode() + "\\" + movieID + "\\" + fileNameList.poll();
                        String url = companyModel.getUrl() + s;
                        saveDownload(url, filePath);
                    }
                }
                if (model != null) {
                    continue;
                }
                videoService.save(videoModel);

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            new File(fileName).delete();
        }
    }

    private void saveDownload(String url, String path) {
        if (new File(path).exists()) {
            return;
        }
        ImageModel imageModel = new ImageModel();
        imageModel.setId(url);
        imageModel.setFilePath(path);
        QueryWrapper<ImageModel> query = Wrappers.query(imageModel);
        if (!imageService.list(query).isEmpty()) {
            return;
        }
        try {
            imageService.save(imageModel);
        } catch (Throwable e) {

        }
    }

    private List<String> getMovieImgUrlsFromJson(String jsonFile) {

        Map jsonData = null;
        try {
            File file = new File(jsonFile);
            jsonData = objectMapper.readValue(file, Map.class);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        List<Map<String, Object>> imgrows = (List<Map<String, Object>>) jsonData.get("Rows");
        if (imgrows == null) {
            return new ArrayList<>(0);
        }
        List<String> list = new ArrayList<>(imgrows.size());
        String img = "";
        for (Map<String, Object> objectMap : imgrows) {
            img = "dyn/dla/images/" + objectMap.get("Img").toString();
            if (objectMap.get("Protected").toString().equalsIgnoreCase("true")) {
                img = img.replace("member", "sample").replace(".jpg", "__@120.jpg");
            }
            list.add(img);
        }
        return list;
    }

}