package com.example.test.mapper;

import com.example.test.config.MybatisRedisCache;
import com.example.test.model.UserModel;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * ${comments}
 * 
 * @author zuochaojie
 * @email chaojie_zuo@qq.com
 * @date 2025-12-23 16:19:58
 */
@Mapper
@CacheNamespace(implementation= MybatisRedisCache.class)
public interface UserMapper extends BaseMapper<UserModel> {

}
