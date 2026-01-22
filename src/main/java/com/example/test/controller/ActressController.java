package com.example.test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.example.test.model.ActressModel;
import com.example.test.service.ActressService;

import com.example.test.utils.R;



/**
 * ${comments}
 *
 * @author zuochaojie
 * @email chaojie_zuo@qq.com
 * @date 2026-01-07 21:00:08
 */
@RestController
@RequestMapping("test/actress")
public class ActressController {
    @Autowired
    private ActressService actressService;

    /**
     * 列表
     */
    @GetMapping()
    public R list(@RequestBody Map<String,Object> params){
        return R.ok(actressService.queryPage(params));
    }


    /**
     * 信息
     */
    @GetMapping("/{id}")
    public R info(@PathVariable("id")Serializable id){
        ActressModel actress = actressService.getById(id);
        return R.ok().put(actress);
    }

    /**
     * 保存
     */
    @PostMapping()
    public R save(@RequestBody ActressModel actress){
		actressService.save(actress);
        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping()
    public R update(@RequestBody ActressModel actress){
		actressService.updateById(actress);
        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public R delete(@PathVariable("id")Serializable id){
		actressService.removeById(id);
        return R.ok();
    }
    @PostMapping("/bentchdelete")
    public R delete(@RequestBody  Serializable[] ids){
        actressService.removeByIds(List.of(ids));
        return R.ok();
    }
}
