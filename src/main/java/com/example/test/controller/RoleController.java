package com.example.test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.Serializable;
import java.util.Map;

import com.example.test.model.RoleModel;
import com.example.test.service.RoleService;

import com.example.test.utils.R;



/**
 * ${comments}
 *
 * @author zuochaojie
 * @email chaojie_zuo@qq.com
 * @date 2025-12-23 16:19:58
 */
@RestController
@RequestMapping("test/role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    /**
     * 列表
     */
    @GetMapping()
    public R list(@RequestBody Map<String,Object> params){
        return R.ok(roleService.queryPage(params));
    }


    /**
     * 信息
     */
    @GetMapping("/{id}")
    public R info(@PathVariable("id")Serializable id){
        RoleModel role = roleService.getById(id);
        return R.ok().put(role);
    }

    /**
     * 保存
     */
    @PostMapping()
    public R save(@RequestBody RoleModel role){
		roleService.save(role);
        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping()
    public R update(@RequestBody RoleModel role){
		roleService.updateById(role);
        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public R delete(@PathVariable("id")Serializable id){
		roleService.removeById(id);
        return R.ok();
    }
}
