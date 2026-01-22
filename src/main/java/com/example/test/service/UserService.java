package com.example.test.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.test.model.UserModel;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Map;
import java.util.Collection;
/**
 * ${comments}
 *
 * @author zuochaojie
 * @email chaojie_zuo@qq.com
 * @date 2025-12-23 16:19:58
 */
public interface UserService extends IService<UserModel> {
     Map<String,Object> queryPage(Map<String,Object> params);

    @Transactional
    Map<String,Object> login(UserModel userModel);

    void logout(HttpServletRequest request);
}

