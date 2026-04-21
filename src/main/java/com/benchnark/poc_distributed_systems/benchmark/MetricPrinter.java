package com.benchnark.poc_distributed_systems.benchmark;

public class MetricPrinter {

    public static void print(String label, long time) {
        System.out.println(label + " -> " + time + " ms");
    }
}
