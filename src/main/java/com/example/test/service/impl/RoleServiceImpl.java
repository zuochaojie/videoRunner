package com.example.test.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.io.Serializable;

import com.example.test.mapper.RoleMapper;
import com.example.test.model.RoleModel;
import com.example.test.service.RoleService;
import org.springframework.transaction.annotation.Transactional;

@Service("roleService")
public class RoleServiceImpl extends ServiceImpl<RoleMapper, RoleModel> implements RoleService {
    @Override
    @Transactional(rollbackFor = {Throwable.class})
    public boolean save(RoleModel model) {
        Map<String, Object> jsonData = new HashMap<>();
        jsonData.put("name", "张三");
        jsonData.put("age", 10);
        model.setJsonData(jsonData);
        baseMapper.insert(model);
        return true;
    }

    @Transactional(rollbackFor = {Throwable.class})
    public boolean saveBatch(Collection<RoleModel> list) {
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
    public boolean updateById(RoleModel model) {
        baseMapper.updateById(model);
        return true;
    }

    @Override
    @Transactional
    public RoleModel getById(Serializable id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional
    public Map<String, Object> queryPage(Map<String, Object> params) {
        int pageSize = Integer.parseInt(params.getOrDefault("pageSize", "10").toString());
        int pageNum = Integer.parseInt(params.getOrDefault("pageNum", 1).toString());
        Page<Object> objects = PageHelper.startPage(pageNum, pageSize);
        PageInfo pageInfo = new PageInfo(baseMapper.selectByMap(params));
        params.put("data,", pageInfo);
        return params;
    }
}