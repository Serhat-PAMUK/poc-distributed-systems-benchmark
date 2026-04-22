package com.benchnark.poc_distributed_systems.cache;

import com.benchnark.poc_distributed_systems.model.CacheData;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("hazelcastCacheService")
public class HazelcastCacheService implements CacheService {

    private final IMap<String, String> map;
    private final StringRedisTemplate redisTemplate;

    public HazelcastCacheService(HazelcastInstance instance, StringRedisTemplate redisTemplate) {
        this.map = instance.getMap("com/benchnark/poc_distributed_systems/cache");
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void put(String key, String value) {
        map.put(key, value);
    }

    @Override
    public String get(String key) {
        return map.get(key);
    }
    @Override
    public void putt(CacheData data) {
        redisTemplate.opsForValue().set(
                data.getKey(),
                data.getValue()
        );

        }
    @Override
    public void putAll(List<CacheData> list) {
        list.forEach(this::putt);
    }
    }

