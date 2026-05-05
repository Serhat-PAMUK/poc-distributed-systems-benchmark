package com.benchnark.poc_distributed_systems.cache;

import com.benchnark.poc_distributed_systems.enums.KvModel;
import com.benchnark.poc_distributed_systems.model.CacheData;
import com.hazelcast.core.HazelcastInstance;import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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

    public void runFailoverTest(String system, int threadCount, KvModel model, int totalCount) {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicLong success = new AtomicLong(0);
        AtomicLong errors = new AtomicLong(0);

        List<Long> latencies = Collections.synchronizedList(new ArrayList<>(totalCount / 10));

        System.out.println(system + " testi başlatılıyor...");

        for (int i = 0; i < totalCount; i++) {
            final int index = i;
            executor.submit(() -> {
                long start = System.nanoTime();
                try {
                    CacheData data = generateOneMillion(model, index);

                    if ("redis".equalsIgnoreCase(system)) {
                        redisTemplate.opsForValue().set(data.getKey(), data.getValue());
                    } else {
                        hazelcastInstance.getMap("benchmarkMap").put(data.getKey(), data.getValue());
                    }

                    long end = System.nanoTime();
                    success.incrementAndGet();
                    latencies.add(end - start); // Gecikmeyi nano saniye olarak ekle

                } catch (Exception e) {
                    errors.incrementAndGet();
                    if (errors.get() % 100 == 0) {
                        System.err.println("Bağlantı Hatası: " + e.getMessage());
                    }
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.HOURS);
            analyzeResults(system, success.get(), errors.get(), latencies);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    private void analyzeResults(String system, long success, long errors, List<Long> latencies) {
        if (latencies.isEmpty()) return;

        Collections.sort(latencies);
        double avg = latencies.stream().mapToLong(Long::longValue).average().orElse(0.0) / 1_000_000.0;
        double p95 = latencies.get((int) (latencies.size() * 0.95)) / 1_000_000.0;
        double p99 = latencies.get((int) (latencies.size() * 0.99)) / 1_000_000.0;

        System.out.println("--- " + system.toUpperCase() + " SONUÇLARI ---");
        System.out.println("Başarılı: " + success);
        System.out.println("Hata: " + errors); // Bu sayı failover süresince artacaktır
        System.out.println("Ortalama Latency: " + avg + " ms");
        System.out.println("P95: " + p95 + " ms");
        System.out.println("P99: " + p99 + " ms");
    }
}
