package com.benchnark.poc_distributed_systems.cache;

import com.benchnark.poc_distributed_systems.model.CacheData;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import java.util.List;


@Service("hazelcastCacheService")

public class HazelcastCacheService implements CacheService {

    private final IMap<String, String> map;

    public HazelcastCacheService(HazelcastInstance hazelcastInstance) {
        this.map = hazelcastInstance.getMap("cache");
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
    public void putt(CacheData datad) {
        map.put(datad.getKey(), datad.getValue());
    }


    @Override
    public void putBatch(List<CacheData> dataList) {
        for (CacheData data : dataList) {
            map.put(data.getKey(), data.getValue());
        }
    }

    @Override
    public int putBatchCorrect(List<CacheData> data) {
            return 0;
    }


}
