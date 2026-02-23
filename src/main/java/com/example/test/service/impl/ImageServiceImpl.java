package com.example.test.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.test.mapper.ImageMapper;
import com.example.test.model.ImageModel;
import com.example.test.service.ImageService;
import com.example.test.service.VideoService;
import com.example.test.utils.Util;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service("imageService")
public class ImageServiceImpl extends ServiceImpl<ImageMapper, ImageModel> implements ImageService {

    public static ThreadPoolExecutor executor = new ThreadPoolExecutor(6, 6,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());
    @Autowired
    private VideoService videoService;

    private int total;

    private AtomicInteger completedTaskCount = new AtomicInteger();

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

    @Scheduled(cron = "0 0 0/1 * * ? ")
    @Override
    public void downloadImg() {
        if (executor.getActiveCount() > 0) {
            return;
        }
        List<ImageModel> list = list();
        Set<String> set = new HashSet<>();
        total = list.size();
        completedTaskCount.set(0);
        for (ImageModel model : list) {
            executor.execute(() -> {
                boolean b = Util.saveUrl2File(model.getId(), model.getFilePath(),1);
                if (b || Util.isNotFount(model.getId())) {
                    completedTaskCount.getAndIncrement();
                    removeById(model.getId());
                }else {
                    String substring = model.getId().substring(model.getId().lastIndexOf("/") + 1);
                    if (!set.contains(substring)){
                        set.add(substring);
                        Util.addThunderTask(model.getId(),model.getFilePath());
                    }
                }
            });

        }
    }

    @Override
    public String getImageTaskProcess() {
        if (total == 0) {
            return "0%";
        }
        BigDecimal totalDecimal = new BigDecimal(total);
        BigDecimal completeDecimal = new BigDecimal(completedTaskCount.get() * 100);
        return completeDecimal.divide(totalDecimal, 1, RoundingMode.HALF_UP).toString() + "%";
    }
}