package com.benchnark.poc_distributed_systems.controller;

import com.benchnark.poc_distributed_systems.cache.CacheBenchmarkService;
import com.benchnark.poc_distributed_systems.enums.KvModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/benchmark")
public class BenchmarkController2 {

    private final CacheBenchmarkService benchmarkService;

    public BenchmarkController2(CacheBenchmarkService benchmarkService) {
        this.benchmarkService = benchmarkService;
    }

    @GetMapping("/start")
    public String startTest(
            @RequestParam String system,      // redis veya hazelcast
            @RequestParam int threads,        // 10, 100, 500
            @RequestParam KvModel model,
            @RequestParam(defaultValue = "1000000") int count) {

        CompletableFuture.runAsync(() ->
                benchmarkService.runFailoverTest(system, threads, model, count)
        );

        return system + " testi " + threads + " thread ile " + model + " veri tipi için başlatıldı. Konsolu izleyin!";
    }
}
