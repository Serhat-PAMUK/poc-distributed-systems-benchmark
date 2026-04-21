package com.benchnark.poc_distributed_systems.cache;

public interface CacheService {
    void put (String key, String value);
    String get(String key);
}
