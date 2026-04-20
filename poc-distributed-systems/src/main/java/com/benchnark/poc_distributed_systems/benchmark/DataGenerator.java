package com.benchnark.poc_distributed_systems.benchmark;

import com.benchnark.poc_distributed_systems.enums.KvModel;

import java.util.UUID;

public class DataGenerator {

    public static String generate(KvModel mode, int i) {

        return switch (mode) {

            case SIMPLE_KV -> simple(i);

            case STRUCTURED_KV -> structured(i);

            case HEAVY_KV -> heavy(i);
        };
    }

    private static String simple(int i) {
        return "{ \"key\":\"user-" + i + "\", \"value\":\"v-" + i + "\" }";
    }

    private static String structured(int i) {
        return "{ \"key\":\"user-" + i + "\", \"value\": { \"name\":\"Ali\", \"age\":30 } }";
    }

    private static String heavy(int i) {
        return "{ \"key\":\"user-" + i + "\", \"value\": { \"profile\": { \"name\":\"Ali\", \"prefs\": { \"theme\":\"dark\" } }, \"orders\":[1,2,3], \"session\": { \"ip\":\"127.0.0.1\" } } }";
    }
}
