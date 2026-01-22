package com.example.test.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.example.test.model.UserModel;
import com.example.test.service.UserService;

import com.example.test.utils.R;



/**
 * ${comments}
 *
 * @author zuochaojie
 * @email chaojie_zuo@qq.com
 * @date 2025-12-23 16:19:58
 */
@RestController
@RequestMapping("test/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 列表
     */
    @GetMapping()
    public R list(@RequestBody Map<String,Object> params){
        return R.ok(userService.queryPage(params));
    }


    /**
     * 信息
     */
    @GetMapping("/{id}")
    public R info(@PathVariable("id")Serializable id){
        UserModel user = userService.getById(id);
        return R.ok().put(user);
    }

    /**
     * 保存
     */
    @PostMapping()
    public R save(@RequestBody UserModel user){
		userService.save(user);
        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping()
    public R update(@RequestBody UserModel user){
		userService.updateById(user);
        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public R delete(@PathVariable("id")Serializable id){
		userService.removeById(id);
        return R.ok();
    }
    @PostMapping("/login")
    public R login(@RequestBody UserModel user){
        Map<String, Object> login = userService.login(user);
        return R.ok().put(login);
    }
    @PostMapping("/logout")
    public R logout(HttpServletRequest request){
        userService.logout(request);
        return R.ok();
    }
    @PostMapping("/bentchdelete")
    public R delete(@RequestBody  Serializable[] ids){
        userService.removeByIds(List.of(ids));
        return R.ok();
    }
}
