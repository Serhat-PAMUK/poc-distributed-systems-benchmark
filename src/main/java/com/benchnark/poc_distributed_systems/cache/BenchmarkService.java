package com.benchnark.poc_distributed_systems.cache;

import com.benchnark.poc_distributed_systems.Factory.DataFactory;
import com.benchnark.poc_distributed_systems.enums.KvModel;
import com.benchnark.poc_distributed_systems.model.CacheData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class BenchmarkService {

    private final CacheService cacheService;

    private static final int TOTAL = 1_000_000;

    public BenchmarkService(CacheService cacheService) {
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
