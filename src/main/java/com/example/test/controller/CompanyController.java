package com.example.test.controller;

import com.example.test.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.example.test.model.CompanyModel;
import com.example.test.service.CompanyService;

import com.example.test.utils.R;


/**
 * ${comments}
 *
 * @author zuochaojie
 * @email chaojie_zuo@qq.com
 * @date 2026-01-10 15:22:33
 */
@RestController
@RequestMapping("test/company")
public class CompanyController {
    @Autowired
    private CompanyService companyService;

    @Autowired
    private ImageService imageService;

    /**
     * 列表
     */
    @GetMapping()
    public R list(@RequestBody Map<String, Object> params) {
        return R.ok(companyService.queryPage(params));
    }

    /**
     * 信息
     */
    @GetMapping("/{id}")
    public R info(@PathVariable("id") Serializable id) {
        CompanyModel company = companyService.getById(id);
        return R.ok().put(company);
    }

    /**
     * 保存
     */
    @PostMapping()
    public R save(@RequestBody CompanyModel company) {
        companyService.save(company);
        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping()
    public R update(@RequestBody CompanyModel company) {
        companyService.updateById(company);
        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public R delete(@PathVariable("id") Serializable id) {
        companyService.removeById(id);
        return R.ok();
    }

    @PostMapping("/bentchdelete")
    public R delete(@RequestBody Serializable[] ids) {
        companyService.removeByIds(List.of(ids));
        return R.ok();
    }

    @GetMapping("refresh")
    public R refresh() {
        companyService.getNewMovie();
        return R.ok();
    }

    @GetMapping("process")
    public R taskProcess() {
        return R.ok().put(companyService.getMovieTaskProcess());
    }
}
