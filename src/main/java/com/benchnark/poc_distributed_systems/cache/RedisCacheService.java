package com.benchnark.poc_distributed_systems.cache;

import com.benchnark.poc_distributed_systems.model.CacheData;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public void putAll(List<CacheData> data) {
        for (CacheData item : data) {
            redis.opsForValue().set(
                    item.getKey(),
                    item.getValue()
            );
        }
    }

    @Override
    public void putt(CacheData data) {
        redis.opsForValue().set(
                data.getKey(),
                data.getValue()
        );
    }

}