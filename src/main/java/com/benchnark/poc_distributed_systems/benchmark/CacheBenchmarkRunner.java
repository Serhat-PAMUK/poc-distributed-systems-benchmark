/*package com.benchnark.poc_distributed_systems.benchmark;

import com.benchnark.poc_distributed_systems.cache.CacheService;

import com.benchnark.poc_distributed_systems.enums.DataModel;
import com.benchnark.poc_distributed_systems.Factory.DataFactory;
import com.benchnark.poc_distributed_systems.config.BenchmarkConfig;
import com.benchnark.poc_distributed_systems.enums.KvModel;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class CacheBenchmarkRunner implements CommandLineRunner {

    private RedisTemplate<String, Object> redisTemplate;
    private final CacheService redisCacheService;
    private final CacheService hazelcastCacheService;


    private final DataModel mode = DataModel.SMALL;
    private final KvModel kvModel = KvModel.HEAVY_KV;
    public CacheBenchmarkRunner(
            @Qualifier("redisCacheService") CacheService redisCacheService,
            @Qualifier("hazelcastCacheService") CacheService hazelcastCacheService
    ) {
        this.redisCacheService = redisCacheService;
        this.hazelcastCacheService = hazelcastCacheService;
    }


    private long getRedisMemory() {
        Properties info = redisTemplate.getRequiredConnectionFactory()
                .getConnection()
                .info("memory");

        return Long.parseLong(info.getProperty("used_memory"));
    }
    @Autowired
    private HazelcastInstance hazelcastInstance;
    private long getHazelcastMemory() {
        IMap<Object, Object> map = hazelcastInstance.getMap("default");

        return map.getLocalMapStats().getHeapCost();
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println("=================================");
        System.out.println("DATA MODE: " + mode);
        System.out.println("THREADS: " + BenchmarkConfig.THREADS);
        System.out.println("OPS: " + BenchmarkConfig.OPERATIONS);
        System.out.println("READ RATIO: " + BenchmarkConfig.READ_RATIO);
        System.out.println("=================================\n");

        System.out.println("=== WARMUP ===");
        warmup(redisCacheService);
        warmup(hazelcastCacheService);

        System.out.println("\n=== REDIS BENCHMARK ===");
        runBenchmark(redisCacheService);

        System.out.println("\n=== HAZELCAST BENCHMARK ===");
        runBenchmark(hazelcastCacheService);
    }

    private void warmup(CacheService cache) {
        for (int i = 0; i < 10_000; i++) {
            var data = DataFactory.generate(mode
                    ,kvModel,i);
            cache.put(data.getKey(), data.getValue());
        }
    }
    private int getOperations(DataModel model) {
        return switch (model) {
            case SMALL -> 10_000;
            case MEDIUM -> 100_000;
            case LARGE -> 500_000;
        };
    }


    private void runBenchmark(CacheService cache) throws Exception {

        ExecutorService executor = Executors.newFixedThreadPool(BenchmarkConfig.THREADS);

        long start = System.currentTimeMillis();
        AtomicInteger counter = new AtomicInteger();
        int ops = getOperations(mode);

        for (int t = 0; t < BenchmarkConfig.THREADS; t++) {

            executor.submit(() -> {
                Random random = new Random();

                while (true) {

                    int id = counter.getAndIncrement();
                    if (id >= ops) break;

                    var data = DataFactory.generate(mode,kvModel, id);

                    if (random.nextDouble() < BenchmarkConfig.READ_RATIO) {
                        cache.get(data.getKey());
                    } else {
                        cache.put(data.getKey(), data.getValue());
                    }
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.MINUTES);
        Thread.sleep(2000);
        System.gc();

            long mem = getRedisMemory();
            System.out.println("Redis Memory: " + mem / (1024 * 1024) + " MB");
            long memes=getHazelcastMemory();
            System.out.println("Hazelcast Memory:" +memes/ (1024*1024) + "MB");

        long end = System.currentTimeMillis();
        double seconds = (end - start) / 1000.0;
        double opsPerSec = ops / seconds;
        System.out.println("\n--- RESULTS ---");
        System.out.println("Total Time: " + (end - start) + " ms");
        System.out.println("Throughput: " + opsPerSec + " ops/sec");


    }
}

 */