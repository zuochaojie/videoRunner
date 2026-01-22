package com.example.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.test.utils.TokenUtil;
import com.github.f4b6a3.uuid.UuidCreator;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.PageHelper;

import jakarta.servlet.http.HttpServletRequest;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import com.example.test.mapper.UserMapper;
import com.example.test.model.UserModel;
import com.example.test.service.UserService;
import org.springframework.transaction.annotation.Transactional;

@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, UserModel> implements UserService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenUtil tokenUtil;
    @Autowired
    private RedissonClient redissonClient;

    @Override
    @Transactional(rollbackFor = {Throwable.class})
    public boolean save(UserModel model) {
        RLock lock = redissonClient.getLock("userService::save");
        try {
            lock.tryLock(10, TimeUnit.SECONDS);
            model.setId(UuidCreator.getTimeOrdered().toString());
            String pass = model.getPass();
            model.setPass(passwordEncoder.encode(pass));
            baseMapper.insert(model);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        return true;
    }

    @Transactional(rollbackFor = {Throwable.class})
    public boolean saveBatch(Collection<UserModel> list) {
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
    public boolean updateById(UserModel model) {
        if (model.getPass() != null) {
            String pass = model.getPass();
            model.setPass(passwordEncoder.encode(pass));
        }
        baseMapper.updateById(model);
        return true;
    }

    @Override
    @Transactional
    public UserModel getById(Serializable id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional
    public Map<String, Object> queryPage(Map<String, Object> params) {
        int pageSize = Integer.parseInt(params.getOrDefault("pageSize", "10").toString());
        int pageNum = Integer.parseInt(params.getOrDefault("pageNum", 1).toString());
        PageHelper.startPage(pageNum, pageSize);
        PageInfo pageInfo = new PageInfo(baseMapper.selectByMap(params));
        params.put("data,", pageInfo);
        return params;
    }

    @Override
    @Transactional
    public Map<String, Object> login(UserModel userModel) {
        String pass = userModel.getPass();
        userModel.setPass(null);
        QueryWrapper<UserModel> query = Wrappers.query(userModel);
        UserModel user = baseMapper.selectOne(query);
        String storedEncryptedPassword = user.getPass();
        boolean matches = passwordEncoder.matches(pass, storedEncryptedPassword);
        Map<String, Object> resMap = new HashMap<>();
        if (matches) {
            String token = tokenUtil.generateToken(userModel.getLoginName());
            resMap.put("token", token);
        }
        return resMap;
    }

    @Override
    @Transactional
    public void logout(HttpServletRequest request) {
        String token = request.getHeader("token");

    }
}