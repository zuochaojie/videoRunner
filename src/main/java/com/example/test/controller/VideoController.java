package com.example.test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.example.test.model.VideoModel;
import com.example.test.service.VideoService;

import com.example.test.utils.R;



/**
 * ${comments}
 *
 * @author zuochaojie
 * @email chaojie_zuo@qq.com
 * @date 2026-01-07 21:00:08
 */
@RestController
@RequestMapping("test/video")
public class VideoController {
    @Autowired
    private VideoService videoService;

    /**
     * 列表
     */
    @GetMapping()
    public R list(@RequestBody Map<String,Object> params){
        return R.ok(videoService.queryPage(params));
    }


    /**
     * 信息
     */
    @GetMapping("/{id}")
    public R info(@PathVariable("id")Serializable id){
        VideoModel video = videoService.getById(id);
        return R.ok().put(video);
    }

    /**
     * 保存
     */
    @PostMapping()
    public R save(@RequestBody VideoModel video){
		videoService.save(video);
        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping()
    public R update(@RequestBody VideoModel video){
		videoService.updateById(video);
        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public R delete(@PathVariable("id")Serializable id){
		videoService.removeById(id);
        return R.ok();
    }
    @PostMapping("/bentchdelete")
    public R delete(@RequestBody  Serializable[] ids){
        videoService.removeByIds(List.of(ids));
        return R.ok();
    }
}
