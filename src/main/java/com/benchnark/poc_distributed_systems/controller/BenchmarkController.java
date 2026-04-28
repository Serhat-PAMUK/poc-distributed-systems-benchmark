package com.benchnark.poc_distributed_systems.controller;

import com.benchnark.poc_distributed_systems.cache.BenchmarkService;
import com.benchnark.poc_distributed_systems.enums.KvModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/benchmark")

public class BenchmarkController {

    private final BenchmarkService benchmarkService;

    public BenchmarkController(BenchmarkService benchmarkService) {
        this.benchmarkService = benchmarkService;
    }

    @PostMapping("/hazelcastBechmark")
    public ResponseEntity<String> bulkLoad(
            @RequestParam KvModel kvModel
    ) {
        benchmarkService.hazelcastBenchmark(kvModel);
        return ResponseEntity.ok("1M data load finished (bulk)");
    }
    @PostMapping("/redisBenchmark")
    public ResponseEntity<String> redisBenchmark(@RequestParam KvModel kvModel){
        benchmarkService.redisBenchmark(kvModel);
        return  ResponseEntity.ok("Redis benchmark parallel");
    }

    @PostMapping("/redisparallel")
    public ResponseEntity<String> parallelLoad(
            @RequestParam KvModel kvModel
    ) {
        benchmarkService.redisParallelBenchmark( kvModel);
        return ResponseEntity.ok("1M data load finished redis(parallel)");
    }
    @PostMapping("/hazelcastparallel")
    public ResponseEntity<String> hazelCastParallel(
        @RequestParam KvModel kvModel){
        benchmarkService.hazelcastParallelBenchmark(kvModel);
        return ResponseEntity.ok("1m finished parallel hazelcast");

}

}
