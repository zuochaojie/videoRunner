package com.example.test.config;

import cn.hutool.extra.spring.SpringUtil;
import org.apache.ibatis.cache.Cache;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;

public class MybatisRedisCache implements Cache {

    private final RReadWriteLock redissonReadWriteLock;
    // redisTemplate
    private final RedisTemplate redisTemplate;
    // 缓存Id
    private final String id;
    //过期时间 10分钟
    private final long expirationTime = 1000 * 60 * 10;

    public MybatisRedisCache(String id) {
        this.id = id;
        //获取redisTemplate
        this.redisTemplate = SpringUtil.getBean("redisTemplate");
        //创建读写锁
        this.redissonReadWriteLock = SpringUtil.getBean(RedissonClient.class).getReadWriteLock("mybatis-cache-lock:" + this.id);
    }
    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void putObject(Object key, Object value) {
        redisTemplate.opsForValue().set(getCacheKey(key), value, expirationTime, TimeUnit.MILLISECONDS);
    }

    @Override
    public Object getObject(Object key) {
        try {
            Object cacheData = redisTemplate.opsForValue().get(getCacheKey(key));
            return cacheData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Object removeObject(Object key) {
        if (key != null) {
            redisTemplate.delete(key.toString());
        }
        return null;
    }

    @Override
    public void clear() {
        Set keys = redisTemplate.keys(getCachePrefix() + ":*");
        redisTemplate.delete(keys);
    }

    @Override
    public int getSize() {
        Long size = (Long) redisTemplate.execute((RedisCallback<Long>) RedisServerCommands::dbSize);
        return size.intValue();
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return this.redissonReadWriteLock;
    }

    public String getCachePrefix() {
        return "mybatis-cache:%s".formatted(this.id);
    }

    private String getCacheKey(Object key) {
        return getCachePrefix() + ":" + key;
    }
}
