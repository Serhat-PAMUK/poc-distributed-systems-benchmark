package com.benchnark.poc_distributed_systems.cache;

import com.benchnark.poc_distributed_systems.enums.KvModel;
import com.benchnark.poc_distributed_systems.model.CacheData;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import static com.benchnark.poc_distributed_systems.Factory.DataFactory.generateOneMillion;

@Service
public class CacheBenchmarkService {

    private final RedisTemplate<String, String> redisTemplate;
    private final HazelcastInstance hazelcastInstance;

    public CacheBenchmarkService(RedisTemplate<String, String> redisTemplate, HazelcastInstance hazelcastInstance) {
        this.redisTemplate = redisTemplate;
        this.hazelcastInstance = hazelcastInstance;
    }

    public void runFailoverTest(int threadCount, KvModel model) {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        IMap<String, String> hzMap = hazelcastInstance.getMap("benchmarkMap");

        AtomicLong success = new AtomicLong(0);
        AtomicLong errors = new AtomicLong(0);
        List<Long> latencies = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < 1000000; i++) {
            final int index = i;
            executor.submit(() -> {
                long start = System.currentTimeMillis();
                try {
                    CacheData data = generateOneMillion(model, index);

                    // TEST EDİLECEK SİSTEM (Burayı parametrik yapabilirsin)
                    // redisTemplate.opsForValue().set(data.key(), data.value());
                    hzMap.put(data.getKey(), data.getValue());

                    success.incrementAndGet();
                    latencies.add(System.currentTimeMillis() - start);
                } catch (Exception e) {
                    errors.incrementAndGet();
                    // Burada hata logu alarak failover'ın ne zaman başladığını anlarız
                }
            });
        }
    }
}
