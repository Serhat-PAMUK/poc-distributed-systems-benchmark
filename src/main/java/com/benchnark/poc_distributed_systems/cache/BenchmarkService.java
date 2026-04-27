package com.benchnark.poc_distributed_systems.cache;

import com.benchnark.poc_distributed_systems.Factory.DataFactory;
import com.benchnark.poc_distributed_systems.enums.KvModel;
import com.benchnark.poc_distributed_systems.model.CacheData;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class BenchmarkService {
    private final HazelcastInstance hzInstance;

    private final CacheService cacheService;

    private static final int TOTAL = 1_000_000;

    public BenchmarkService(HazelcastInstance hzInstance, CacheService cacheService) {
        this.hzInstance = hzInstance;
        this.cacheService = cacheService;
    }

    public void bulkLoad(KvModel kvModel) {

        long start = System.currentTimeMillis();

        List<CacheData> batch = new ArrayList<>(TOTAL);

        for (int i = 0; i < TOTAL; i++) {
            batch.add(DataFactory.generateOneMillion(kvModel,i));

            if ((i + 1) % 1000 == 0) {
                System.out.println("✅ " + (i + 1) + " / " + TOTAL + " veri oluşturuldu");
            }
        }

        System.out.println("📤 Veriler gönderiliyor...");
        cacheService.putBatch(batch);
        System.out.println("fonksiyona girdi");



        long end = System.currentTimeMillis();

        System.out.println("🎉 BULK LOAD TIME: " + (end - start) + " ms");
    }
    public void hazelcastBenchmark(KvModel kvModel) {
        System.out.println("🚀 Hazelcast Benchmark Başlatıldı...");
        long start = System.currentTimeMillis();


        IMap<String, String> map = hzInstance.getMap("benchmarkMap");

        Map<String, String> batchMap = new HashMap<>();

        for (int i = 0; i < TOTAL; i++) {
            CacheData data = DataFactory.generateOneMillion(kvModel, i);
            batchMap.put(data.getKey(), data.getValue());

            if ((i + 1) % 10000 == 0) {
                map.putAll(batchMap);
                batchMap.clear();
            }
        }
        if (!batchMap.isEmpty()) {
            map.putAll(batchMap);
        }

        long end = System.currentTimeMillis();
        System.out.println("⏱ Hazelcast Toplam Süre: " + (end - start) + " ms");
    }

    public void redisBenchmark(KvModel kvModel) {

        int[] batchSizes = {500000,750000,1000000};

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

    public void parallelLoad( KvModel kvModel) {

        long start = System.currentTimeMillis();

        int threads = 8;
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        int batchSize = TOTAL / threads;

        List<Future<?>> futures = new ArrayList<>();

        for (int t = 0; t < threads; t++) {

            int startIdx = t * batchSize;
            int endIdx = (t == threads - 1) ? TOTAL : (t + 1) * batchSize;

            futures.add(executor.submit(() -> {
                for (int i = startIdx; i < endIdx; i++) {
                    CacheData data = DataFactory.generateOneMillion( kvModel,i );
                    cacheService.putt(data);
                }
            }));
        }

        for (Future<?> f : futures) {
            try {
                f.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();

        long end = System.currentTimeMillis();

        System.out.println("PARALLEL LOAD TIME: " + (end - start) + " ms");
    }
}
