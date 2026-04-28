package com.benchnark.poc_distributed_systems.cache;

import com.benchnark.poc_distributed_systems.Factory.DataFactory;
import com.benchnark.poc_distributed_systems.enums.KvModel;
import com.benchnark.poc_distributed_systems.model.CacheData;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class BenchmarkService {
    private final HazelcastInstance hzInstance;

    private final CacheService cacheService;

    private static final int TOTAL = 1_000_000;

    public BenchmarkService(HazelcastInstance hzInstance, CacheService cacheService) {
        this.hzInstance = hzInstance;
        this.cacheService = cacheService;
    }

    public void hazelcastBenchmark(KvModel kvModel) {
        int[] batchSizes = {10000,15000,25000,40000,70000, 100000,200000,400000,500000};

        for (int batchSize : batchSizes) {
            System.out.println("\n--- [HAZELCAST BENCHMARK START] Batch Size: " + batchSize + " ---");

            IMap<String, String> map = hzInstance.getMap("benchmarkMap");

            map.clear();

            long start = System.currentTimeMillis();

            Map<String, String> localBatch = new HashMap<>(batchSize);

            for (int i = 0; i < TOTAL; i++) {
                CacheData data = DataFactory.generateOneMillion(kvModel, i);
                localBatch.put(data.getKey(), data.getValue());

                if ((i + 1) % batchSize == 0) {
                    map.putAll(localBatch);
                    localBatch.clear(); // Listeyi boşalt ki RAM şişmesin
                }

                // Her 50k'da bir durum logu
                if ((i + 1) % 50000 == 0) {
                    System.out.println("➤ Hazelcast: " + (i + 1) + " / " + TOTAL);
                }
            }

            // Eğer son pakette batchSize'dan az veri kaldıysa onları da gönder
            if (!localBatch.isEmpty()) {
                map.putAll(localBatch);
            }

            long end = System.currentTimeMillis();
            double durationSeconds = (end - start) / 1000.0;
            System.out.println("✅ [HAZELCAST COMPLETED]");
            System.out.println("⏱ Toplam Süre: " + durationSeconds + " sn");
            System.out.println("🚀 Hız: " + (int)(TOTAL / durationSeconds) + " ops/sec");
        }
    }

    public void redisBenchmark(KvModel kvModel) {

        int[] batchSizes = {10000,15000,25000,40000,70000, 100000,200000,400000,500000};

        for (int batchSize : batchSizes) {

            System.out.println("\n--- [REDIS BENCHMARK START] Batch Size: " + batchSize + " ---");

            long start = System.currentTimeMillis();

            try (Jedis jedis = new Jedis("redis://:Serhat1234.@localhost:6379"))
                 {

                jedis.flushDB();

                Pipeline p = jedis.pipelined();

                for (int i = 0; i < TOTAL; i++) {

                    CacheData data = DataFactory.generateOneMillion(kvModel, i);
                    p.set(data.getKey(), data.getValue());

                    if ((i + 1) % batchSize == 0) {
                        p.sync();
                    }

                    if ((i + 1) % 50000 == 0) {
                        System.out.println("➤ Redis: " + (i + 1) + " / " + TOTAL);
                    }
                }

                p.sync();
            }

            long end = System.currentTimeMillis();
            double durationSeconds = (end - start) / 1000.0;

            System.out.println("✅ COMPLETED | Batch: " + batchSize);
            System.out.println("⏱ Süre: " + durationSeconds + " sn");
            System.out.println("🚀 Hız: " + (int)(TOTAL / durationSeconds) + " ops/sec");
        }
    }
    public void redisParallelBenchmark(KvModel kvModel) {
        int[] batchSizes = {10000, 15000, 25000, 40000, 70000, 100000, 200000, 400000, 500000};
        int threadCount = 2;

        for (int currentBatchSize : batchSizes) {
            System.out.println("\n--- [REDIS PARALLEL TEST] Batch: " + currentBatchSize + " | Thread: 8 ---");
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(64);

            // Her batch testi öncesi DB temizliği (isteğe bağlı)
            try (Jedis cleanJedis = new Jedis("localhost", 6379, 60000, 60000)) {
                cleanJedis.auth("Serhat1234.");
                cleanJedis.flushDB();
                System.out.println("✅ Redis DB başarıyla temizlendi.");

                ExecutorService executor = Executors.newFixedThreadPool(threadCount);
                int totalPerThread = TOTAL / threadCount;
                long start = System.currentTimeMillis();

                for (int t = 0; t < threadCount; t++) {
                    final int startIdx = t * totalPerThread;
                    final int endIdx = (t == threadCount - 1) ? TOTAL : (t + 1) * totalPerThread;

                    executor.submit(() -> {
                        try (Jedis jedis = new Jedis("redis://:Serhat1234.@localhost:6379")) {
                            Pipeline p = jedis.pipelined();
                            for (int i = startIdx; i < endIdx; i++) {
                                CacheData data = DataFactory.generateOneMillion(kvModel, i);
                                p.set(data.getKey(), data.getValue());

                                if ((i - startIdx + 1) % currentBatchSize == 0) {
                                    p.sync();
                                }
                            }
                            p.sync();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }

                executor.shutdown();
                try {
                    executor.awaitTermination(15, TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                long end = System.currentTimeMillis();
                System.out.println("✅ Batch " + currentBatchSize + " Bitti. Süre: " + (end - start) + " ms");
            }
        }
    }
     public void hazelcastParallelBenchmark(KvModel kvModel) {
        int[] batchSizes = {10000, 15000, 25000, 40000, 70000, 100000, 200000, 400000, 500000};
        int threadCount = 8;

        for (int currentBatchSize : batchSizes) {
            System.out.println("\n--- [HAZELCAST PARALLEL TEST] Batch: " + currentBatchSize + " | Thread: 8 ---");

            IMap<String, String> map = hzInstance.getMap("benchmarkMap");
            map.clear(); // Her batch testi öncesi temizlik

            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            int totalPerThread = TOTAL / threadCount;
            long start = System.currentTimeMillis();

            for (int t = 0; t < threadCount; t++) {
                final int startIdx = t * totalPerThread;
                final int endIdx = (t == threadCount - 1) ? TOTAL : (t + 1) * totalPerThread;

                executor.submit(() -> {
                    Map<String, String> localBatch = new HashMap<>(currentBatchSize);
                    for (int i = startIdx; i < endIdx; i++) {
                        CacheData data = DataFactory.generateOneMillion(kvModel, i);
                        localBatch.put(data.getKey(), data.getValue());

                        if (localBatch.size() >= currentBatchSize) {
                            map.putAll(localBatch);
                            localBatch.clear();
                        }
                    }
                    if (!localBatch.isEmpty()) {
                        map.putAll(localBatch);
                    }
                });
            }

            executor.shutdown();
            try {
                executor.awaitTermination(15, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            long end = System.currentTimeMillis();
            System.out.println("✅ Batch " + currentBatchSize + " Bitti. Süre: " + (end - start) + " ms");
        }
    }



}
