package com.benchnark.poc_distributed_systems.controller;

import com.benchnark.poc_distributed_systems.cache.BenchmarkService;
import com.benchnark.poc_distributed_systems.enums.DataModel;
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

    @PostMapping("/bulk-load")
    public ResponseEntity<String> bulkLoad(
            @RequestParam KvModel kvModel
    ) {
        benchmarkService.bulkLoad(kvModel);
        return ResponseEntity.ok("1M data load finished (bulk)");
    }
    @PostMapping("/parallel-load")
    public ResponseEntity<String> parallelLoad(
            @RequestParam KvModel kvModel
    ) {
        benchmarkService.parallelLoad( kvModel);
        return ResponseEntity.ok("1M data load finished (parallel)");
    }
}
