package com.benchnark.poc_distributed_systems.cache;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service("redisCacheService")
public class RedisCacheService implements CacheService {

    private final StringRedisTemplate redis;

    public RedisCacheService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public void put(String key, String value) {
        redis.opsForValue().set(key, value);
    }

    @Override
    public String get(String key) {
        return redis.opsForValue().get(key);
    }
}