package com.benchnark.poc_distributed_systems.cache;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("hazelcastCacheService")
public class HazelcastCacheService implements CacheService {

    private final IMap<String, String> map;

    public HazelcastCacheService(HazelcastInstance instance) {
        this.map = instance.getMap("com/benchnark/poc_distributed_systems/cache");
    }

    @Override
    public void put(String key, String value) {
        map.put(key, value);
    }

    @Override
    public String get(String key) {
        return map.get(key);
    }
}
