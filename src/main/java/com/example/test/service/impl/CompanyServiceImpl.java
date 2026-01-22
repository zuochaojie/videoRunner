package com.example.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.test.message.MessageData;
import com.example.test.message.RocketMQProducer;
import com.example.test.model.ActressModel;
import com.example.test.model.ImageModel;
import com.example.test.model.VideoModel;
import com.example.test.service.ActressService;
import com.example.test.service.ImageService;
import com.example.test.service.VideoService;
import com.example.test.utils.Util;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.uuid.UuidCreator;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import jakarta.annotation.PostConstruct;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import java.io.*;
import java.sql.Date;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import com.example.test.mapper.CompanyMapper;
import com.example.test.model.CompanyModel;
import com.example.test.service.CompanyService;
import org.springframework.transaction.annotation.Transactional;

@Service("companyService")
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, CompanyModel> implements CompanyService {
    @Autowired
    private VideoService videoService;
    @Autowired
    private ActressService actressService;
    @Autowired
    private RocketMQProducer rocketMQProducer;

    @Autowired
    private ImageService imageService;
    public static final String IMG_DIR = "D:\\document\\static\\images\\";
    public static final String TEMP_DOWNLOAD_DIR = "D:\\迅雷下载\\";
    public static ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 4,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());

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
    public void getNewMovie() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                caribeanTask();
            }
        });
        executor.execute(new Runnable() {
            @Override
            public void run() {
                pacopacomamaTask();
            }
        });
        executor.execute(new Runnable() {
            @Override
            public void run() {
//                pondoTask();
                avsox1PondoTask();
            }
        });
        executor.execute(new Runnable() {
            @Override
            public void run() {
//                heyzoTask();
                avsoxHeyzoTask();
            }
        });
    }

    private void pacopacomamaTask() {
        CompanyModel companyModel = getById("pacopacomama");
        this.getJsonMovie(companyModel);
    }

    private void pondoTask() {
        CompanyModel companyModel = getById("1pondo");
        this.getJsonMovie(companyModel);
    }

    private void avsoxHeyzoTask() {
        String str = "http://avsox.click/ja/studio/74a9d0e356f0b5b8";
        String tempHtml = TEMP_DOWNLOAD_DIR + "heyzo_avsox.html";
        boolean success = Util.saveUrl2File(str, tempHtml, 10000);
        if (!success) {
            return;
        }
        try {
            Document parse = Jsoup.parse(new File(tempHtml));
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
                Document document = Jsoup.parse(new File(movieHtml));
                String[] strings = document.select(".genre").stream().map(Element::text).toArray(String[]::new);
                String actress = document.select(".avatar-box").get(0).text();
                videoModel.setTags(strings);
                videoModel.setActress(actress);
                videoModel.setId(UuidCreator.getTimeOrdered().toString());
                String releaseName = document.select("h3").get(0).text();
                if (releaseName.contains("】")) {
                    releaseName = releaseName.substring(releaseName.indexOf("】") + 1).trim();
                }
                releaseName = releaseName.replace(title,"").replace("～ - 無修正アダルト動画 HEYZO", "")
                        .replace("- 無修正アダルト動画 HEYZO", "").trim();
                videoModel.setReleaseName(releaseName);
                videoService.save(videoModel);
                List<String> heyzoImgUrl = getHeyzoImgUrl(title);
                ImageModel imageModel = new ImageModel();
                imageModel.setId(document.select(".bigImage").get(0).attr("href"));
                imageModel.setFilePath(TEMP_DOWNLOAD_DIR + "HEYZO\\" + videoModel.getTitle() + "\\" + videoModel.getTitle() + ".jpg");
                if (imageService.getById(imageModel.getId()) == null) {
                    imageService.save(imageModel);
                }
                Queue<String> fileNameList = getFileNameList();
                for (String s : heyzoImgUrl) {
                    imageModel.setId(s);
                    imageModel.setFilePath(TEMP_DOWNLOAD_DIR + "HEYZO\\" + videoModel.getTitle() + "\\" + fileNameList.poll());
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
        boolean success = Util.saveUrl2File(str, tempHtml, 1000);
        if (!success) {
            return;
        }
        try {
            Document parse = Jsoup.parse(new File(tempHtml));
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
                Document document = Jsoup.parse(new File(movieHtml));
                String releaseName = document.select("h3").get(0).text().replace(title,"").trim();
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
                    imageModel.setFilePath(TEMP_DOWNLOAD_DIR + "1pondo\\" + videoModel.getTitle() + "\\" + videoModel.getTitle() + ".jpg");
                    if (imageService.getById(imageModel.getId()) == null) {
                        imageService.save(imageModel);
                    }
                    Queue<String> fileNameList = getFileNameList();
                    for (String s : images) {
                        imageModel.setId("https://www.1pondo.tv/" + s);
                        imageModel.setFilePath(TEMP_DOWNLOAD_DIR + "1pondo\\" + videoModel.getTitle() + "\\" + fileNameList.poll());
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
        boolean success = Util.saveUrl2File(companyModel.getNewMoviePage(), tempFile);
        if (!success) {
            saveFailedDownload(companyModel.getNewMoviePage(), tempFile);
            return;
        }
        File htmlFlie = new File(tempFile);
        Document document = null;
        try {
            document = Jsoup.parse(htmlFlie);
            htmlFlie.delete();
            Element entrys = document.select(".movie-list").get(0);
            Elements children = entrys.children();
            for (Element child : children) {
                String movieId = child.attr("data-movie-id");
                VideoModel videoModel = new VideoModel();
                videoModel.setTitle("HEYZO-" + movieId);
                videoModel.setProducedBy(companyModel.getCode().toUpperCase(Locale.ROOT));
                String releaseName = child.select(".lazy").attr("title");
                VideoModel model = videoService.getBaseMapper().selectOne(Wrappers.query(videoModel));
                if (model != null) {
                    return;
                }
                String actress = child.select(".actor").get(0).text();
                String releaseDate = child.select(".release").get(0).text().replace("公開日:", "").trim();
                String imgUrl = companyModel.getMovieImage().replace("xxxx", movieId);
                String downLoadimg = IMG_DIR + companyModel.getCode() + "\\" + videoModel.getTitle() + "\\" + videoModel.getTitle() + ".jpg";
                String movieDetails = companyModel.getMovieDetails();
                String detailUrl = movieDetails.replace("xxxx", movieId);
                success = Util.saveUrl2File(imgUrl, downLoadimg);
                if (!success) {
                    saveFailedDownload(imgUrl, downLoadimg);
                }
                String tempHtml = TEMP_DOWNLOAD_DIR + movieId + ".html";
                success = Util.saveUrl2File(detailUrl, tempHtml);
                if (!success) {
                    saveFailedDownload(detailUrl, tempHtml);
                }
                File file = new File(tempHtml);
                Document parse = Jsoup.parse(file);
                String[] strings = parse.select(".tag-keyword-list").stream().map(Element::text).toArray(String[]::new);
                videoModel.setTags(strings);
                file.delete();
                videoModel.setActress(actress);
                videoModel.setReleaseDate(Date.valueOf(releaseDate));
                videoModel.setId(UuidCreator.getTimeOrdered().toString());
                videoModel.setReleaseName(releaseName);
                videoService.save(videoModel);
                List<String> heyzoImgUrl = getHeyzoImgUrl(movieId);
                for (String s : heyzoImgUrl) {
                    downLoadimg = IMG_DIR + companyModel.getCode() + "\\" + videoModel.getTitle() + "\\" + s.substring(s.lastIndexOf("/") + 1);
                    String temp = downLoadimg;
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            boolean b = Util.saveUrl2File(s, temp);
                            if (!b) {
                                saveFailedDownload(s, temp);
                            }
                        }
                    });
                }
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
            saveFailedDownload(newMoviePage, tempFile);
            return;
        }
        File htmlFlie = new File(tempFile);
        try {
            Document document = Jsoup.parse(htmlFlie);
            htmlFlie.delete();
            Elements elements = document.select(".media-image");
            List<ImageModel> list = new ArrayList<>();
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
                String tempHtml = TEMP_DOWNLOAD_DIR + title + ".html";
                videoModel.setReleaseName(releaseName);
                videoModel.setId(UuidCreator.getTimeOrdered().toString());
                String imgUrl = companyModel.getMovieImage().replace("xxxx", title);
                String downLoadimg = IMG_DIR + companyModel.getCode() + File.separator + title + File.separator + title + ".jpg";
                success = Util.saveUrl2File(imgUrl, downLoadimg);
                if (!success) {
                    saveFailedDownload(imgUrl, downLoadimg);
                }
                success = Util.saveUrl2File(detailUrl, tempHtml);
                if (success) {
                    File file = new File(tempHtml);
                    Document parse = Jsoup.parse(file);
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
                        String s = imgUrl;
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                boolean b = Util.saveUrl2File(s, viewImg);
                                if (!b) {
                                    saveFailedDownload(s, viewImg);
                                }
                            }
                        });
                    }
                    Elements select = parse.select(".spec-content");
                    String actress = select.get(0).text();
                    String text = select.get(1).text().replace("/", "-");
                    try {
                        videoModel.setReleaseDate(Date.valueOf(text));
                    } catch (Exception e) {

                    }
                    String[] strings = parse.select(".spec-item").stream().map(Element::text).toArray(String[]::new);
                    videoModel.setTags(strings);
                    videoModel.setActress(actress);
                    file.delete();
                } else {
                    saveFailedDownload(detailUrl, tempHtml);
                }
                videoService.save(videoModel);
            }
        } catch (Exception e) {

        }
    }

    private List<String> getHeyzoImgUrl(String movieId) {
        List<String> list = new ArrayList<>(22);
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/gallery/001.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/gallery/002.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/gallery/003.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/thumbnail_004.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/thumbnail_005.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/thumbnail_006.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/thumbnail_007.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/thumbnail_008.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/thumbnail_009.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/thumbnail_010.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/thumbnail_011.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/thumbnail_012.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/thumbnail_013.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/thumbnail_014.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/thumbnail_015.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/thumbnail_016.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/thumbnail_017.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/thumbnail_018.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/thumbnail_019.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/thumbnail_020.jpg");
        list.add("https://www.heyzo.com/contents/3000/" + movieId + "/thumbnail_021.jpg");
        return list;
    }

    private void getJsonMovie(CompanyModel companyModel) {
        String fileName = TEMP_DOWNLOAD_DIR + companyModel.getCode() + ".json";
        boolean success = Util.saveUrl2File(companyModel.getNewMoviePage(), fileName);
        if (!success) {
            saveFailedDownload(companyModel.getNewMoviePage(), fileName);
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
                if (model != null) {
                    return;
                }
                String actor = row.get("Actor").toString();
                String release = row.get("Release").toString();
                String releaseName = row.get("Title").toString();
                List<String> list = (List<String>) row.get("UCNAME");
                String[] tags = new String[list.size()];
                list.toArray(tags);
                String imgurl = row.get("ThumbHigh").toString();
                String imgFile = IMG_DIR + companyModel.getCode() + "\\" + movieID + "\\" + movieID + ".jpg";
                success = Util.saveUrl2File(imgurl, imgFile);
                if (!success) {
                    saveFailedDownload(imgurl, imgFile);
                }
                videoModel.setReleaseDate(Date.valueOf(release));
                videoModel.setActress(actor);
                videoModel.setTags(tags);
                videoModel.setReleaseName(releaseName);
                videoModel.setId(UuidCreator.getTimeOrdered().toString());
                videoService.save(videoModel);
                String tempjson = TEMP_DOWNLOAD_DIR + companyModel.getCode() + movieID + ".json";
                String imgsJson = companyModel.getUrl() + "dyn/dla/json/movie_gallery/" + movieID + ".json";
                success = Util.saveUrl2File(imgsJson, tempjson);
                if (success) {
                    list = getMovieImgUrlsFromJson(tempjson);
                    Queue<String> fileNameList = getFileNameList();
                    new File(tempjson).delete();
                    for (String s : list) {
                        String url = companyModel.getUrl() + s;
                        imgFile = IMG_DIR + companyModel.getCode() + "\\" + movieID + "\\" + fileNameList.poll();
                        String filePath = imgFile;
                        boolean b = Util.saveUrl2File(url, filePath);
                        if (!b) {
                            saveFailedDownload(url, filePath);
                        }
                    }
                } else {
                    saveFailedDownload(imgsJson, tempjson);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            new File(fileName).delete();
        }
    }

    private void saveFailedDownload(String url, String path) {
        ImageModel imageModel = new ImageModel();
        imageModel.setId(url);
        imageModel.setFilePath(path);
        try {
            imageService.save(imageModel);
        } catch (Throwable e) {

        }
    }

    private List<String> getMovieImgUrlsFromJson(String jsonFile) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map jsonData = null;
        try {
            jsonData = objectMapper.readValue(new File(jsonFile), Map.class);
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