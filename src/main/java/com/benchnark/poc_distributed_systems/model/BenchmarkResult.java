package com.benchnark.poc_distributed_systems.model;

public record BenchmarkResult(
        String systemName,     // Redis veya Hazelcast
        int threadCount,
        long totalAttempted,
        long successCount,
        long errorCount,
        double p95LatencyMs,
        double p99LatencyMs,
        long failoverTimeMs    // Kesinti süresi
) {}