package com.benchnark.poc_distributed_systems.cache;

import com.benchnark.poc_distributed_systems.model.CacheData;

import java.util.List;

public interface CacheService {
    void put (String key, String value);
    String get(String key);
    void putt(CacheData data);
    void putBatch(List<CacheData> data);
    int putBatchCorrect(List<CacheData> data);
}
