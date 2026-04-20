package com.benchnark.poc_distributed_systems.config;

public class BenchmarkConfig {

    public static final int THREADS = 8;
    public static final int OPERATIONS = 100_000;

    // %80 read - %20 write
    public static final double READ_RATIO = 0.8;
}
