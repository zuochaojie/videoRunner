package com.example.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.test.mapper.VideoMapper;
import com.example.test.message.MessageData;
import com.example.test.message.RocketMQProducer;
import com.example.test.model.VideoModel;
import com.example.test.page.RenRenHtmlPage;
import com.example.test.service.ActressService;
import com.example.test.service.VideoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.uuid.UuidCreator;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("videoService")
@EnableAspectJAutoProxy(exposeProxy = true)
public class VideoServiceImpl extends ServiceImpl<VideoMapper, VideoModel> implements VideoService {
    @Value("${filepath.tempdownload}")
    public String TEMP_DOWNLOAD_DIR;
    @Autowired
    private ActressService actressService;
    @Autowired
    private RocketMQProducer rocketMQProducer;

    @Override
    @Transactional(rollbackFor = {Throwable.class}, propagation = Propagation.REQUIRES_NEW)
    public boolean save(VideoModel model) {
        String[] tags = model.getTags();
        List<String> newList = new ArrayList<>(tags.length);
        for (String tag : tags) {
            if ("1080p".equals(tag) || "60fps".equals(tag) || tag.endsWith("代") || tag.toLowerCase().contains("vip") || "720p".equals(tag)) {
                continue;
            }
            newList.add(tag);
        }
        model.setTags(newList.toArray(String[]::new));
        baseMapper.insert(model);
        return true;
    }

    @Transactional(rollbackFor = {Throwable.class})
    public boolean saveBatch(Collection<VideoModel> list) {
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
    @Transactional(rollbackFor = {Throwable.class}, propagation = Propagation.REQUIRES_NEW)
    public boolean updateById(VideoModel model) {
        baseMapper.updateById(model);
        return true;
    }

    @Override
    @Transactional
    public VideoModel getById(Serializable id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional
    public Map<String, Object> queryPage(Map<String, Object> params) {
        int pageSize = Integer.parseInt(params.getOrDefault("pageSize", "10").toString());
        int pageNum = Integer.parseInt(params.getOrDefault("pageNum", 1).toString());
        Page<Object> objects = PageHelper.startPage(pageNum, pageSize);
        PageInfo pageInfo = new PageInfo(baseMapper.selectByMap(params));
        params.put("data", pageInfo.getList());
        return params;
    }

    @Override
    @Transactional
    public void updateAddress(Map<String, String> params) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DATE, -1);
        String endDate = sdf.format(instance.getTime());
        instance.add(Calendar.DATE, -10);
        String startDate = sdf.format(instance.getTime());
        String end = params.getOrDefault("endDate", endDate);
        String start = params.getOrDefault("startDate", startDate);
        LambdaQueryWrapper<VideoModel> wrapper = Wrappers.lambdaQuery(VideoModel.class).ge(VideoModel::getReleaseDate, Date.valueOf(start))
                .le(VideoModel::getReleaseDate, Date.valueOf(end));
        List<VideoModel> list = list(wrapper);
        if (list.isEmpty()) {
            return;
        }
        new Thread(() -> {
            try (RenRenHtmlPage page = new RenRenHtmlPage()) {
                page.initPage();
                Map<String, List<String>> magicList = page.search(list,start);
                ObjectMapper mapper = new ObjectMapper();
                String string = mapper.writeValueAsString(magicList);
                MessageData messageData = new MessageData();
                messageData.setContent(string);
                messageData.setId(UuidCreator.getTimeOrdered().toString());
                rocketMQProducer.sendAsyncMessage(messageData);
            } catch (Exception e) {
                System.out.println(e);
            }
        }).start();

    }

    private void importAddress(File file) {
        if (!file.exists()) {
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, List<String>> map = mapper.readValue(file, Map.class);
            map.forEach((k, v) -> {
                VideoModel model = new VideoModel();
                model.setId(k);
                model.setAddress(v.toArray(new String[0]));
                updateById(model);
            });
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}