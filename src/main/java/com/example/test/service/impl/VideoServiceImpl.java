package com.example.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.test.mapper.ActressMapper;
import com.example.test.model.ActressModel;
import com.example.test.service.ActressService;
import com.example.test.utils.Util;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.uuid.UuidCreator;
import com.opencsv.CSVReader;
import org.springframework.aop.framework.AopContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import com.example.test.mapper.VideoMapper;
import com.example.test.model.VideoModel;
import com.example.test.service.VideoService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("videoService")
@EnableAspectJAutoProxy(exposeProxy = true)
public class VideoServiceImpl extends ServiceImpl<VideoMapper, VideoModel> implements VideoService {

    @Autowired
    private ActressService actressService;

    @Override
    @Transactional(rollbackFor = {Throwable.class}, propagation = Propagation.REQUIRES_NEW)
    public boolean save(VideoModel model) {
        String[] tags = model.getTags();
        List<String> newList = new ArrayList<>(tags.length);
        for (String tag : tags) {
            if ("1080p".equals(tag) || "60fps".equals(tag) || tag.endsWith("代") || tag.toLowerCase().contains("vip") || "720p".equals(tag)){
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


    public String[] getVideoMagicAdress(String title, String productBy) {
        String searchUlr = "https://btsow.lol/bts/data/api/search";
        ObjectMapper objectMapper = new ObjectMapper();
        String keyWords = title;
        if (productBy.equals("caribbeancom") || productBy.equals("1pondo")) {
            keyWords = title + productBy.substring(0, 4);
        }
        String result = Util.postSearch(keyWords, searchUlr);
        try {
            Map<String, Object> map = objectMapper.readValue(result, Map.class);
            Object data = map.get("data");
            if (data instanceof List<?>) {
                List<Map<String, String>> dataList = (List<Map<String, String>>) data;
                int size = dataList.size();
                String[] array = new String[size];
                List<String> collect = dataList.stream().map(json -> json.get("hash")).collect(Collectors.toList());
                collect.toArray(array);
                return array;
            }
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
        }
        return new String[0];
    }
}