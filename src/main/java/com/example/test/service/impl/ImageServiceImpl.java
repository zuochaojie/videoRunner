package com.example.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.test.utils.Util;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.io.Serializable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.example.test.mapper.ImageMapper;
import com.example.test.model.ImageModel;
import com.example.test.service.ImageService;
import org.springframework.transaction.annotation.Transactional;

@Service("imageService")
public class ImageServiceImpl extends ServiceImpl<ImageMapper, ImageModel> implements ImageService {

    public static ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 4,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());

    @Override
    @Transactional(rollbackFor = {Throwable.class})
    public boolean save(ImageModel model) {
        baseMapper.insert(model);
        return true;
    }

    @Transactional(rollbackFor = {Throwable.class})
    public boolean saveBatch(Collection<ImageModel> list) {
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
    public boolean updateById(ImageModel model) {
        baseMapper.updateById(model);
        return true;
    }

    @Override
    @Transactional
    public ImageModel getById(Serializable id) {
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

    private boolean downloadImgRunning = false;
    @Scheduled(cron = "0 0 0/1 * * ? ")
    @Override
    public void downloadImg() {
        if (downloadImgRunning){
            return;
        }
        String array[] = {"caribbeancom", "pacopacomama", "1pondo", "heyzo"};
        downloadImgRunning = true;
        for (String s : array) {
            System.out.println(s+" 图片下载任务开始。。。");
            QueryWrapper<ImageModel> wrapper = Wrappers.query(new ImageModel()).like("id", s);
            List<ImageModel> list = list(wrapper);
            for (ImageModel model : list) {
                boolean b = Util.saveUrl2File(model.getId().replace("https:https:","https:"), model.getFilePath(), 1000);
                if (b || Util.isNotFount(model.getId())) {
                    removeById(model.getId());
                }
            }
        }
        downloadImgRunning = false;
    }
}