package com.benchnark.poc_distributed_systems.Factory;

import com.benchnark.poc_distributed_systems.enums.DataModel;
import com.benchnark.poc_distributed_systems.enums.KvModel;
import com.benchnark.poc_distributed_systems.model.CacheData;

public class DataFactory {

    public static CacheData generate( DataModel model,KvModel kvModel, int i) {

        String key = "user-" + i;

        String value = switch (kvModel) {

            case SIMPLE_KV -> simple(i);
            case STRUCTURED_KV -> structured(i);
            case HEAVY_KV -> heavy(i);
        };

        return new CacheData(key, value);
    }
    public static CacheData generateOneMillion(KvModel kvModel, int i) {
        String key = "user-" + i;

        String value = switch (kvModel) {
            case SIMPLE_KV -> simple(i);
            case STRUCTURED_KV -> structured(i);
            case HEAVY_KV -> heavy(i);
        };

        return new CacheData(key, value);
    }


    private static String simple(int i) {
        return "{"
                + "\"id\":" + i + ","
                + "\"key\":\"user-" + i + "\","
                + "\"value\":\"v-" + i + "\""
                + "}";
    }


    private static String structured(int i) {
        return "{"
                + "\"id\":" + i + ","
                + "\"user\":{"
                + "\"name\":\"Ali-" + i + "\","
                + "\"age\":30,"
                + "\"email\":\"user" + i + "@mail.com\""
                + "},"
                + "\"metrics\":{"
                + "\"loginCount\":10,"
                + "\"lastLogin\":\"2026-04-20T10:00:00\","
                + "\"active\":true"
                + "},"
                + "\"tags\":[\"dev\",\"test\",\"cache\"]"
                + "}";
    }


    private static String heavy(int i) {

        return "{"
                + "\"id\":" + i + ","
                + "\"user\":{"
                + "\"profile\":{"
                + "\"name\":\"Ali-" + i + "\","
                + "\"surname\":\"Yilmaz\","
                + "\"bio\":\"" + "x".repeat(200) + "\""
                + "},"
                + "\"addresses\":["
                + addressBlock("Istanbul", "Kadikoy")
                + ","
                + addressBlock("Ankara", "Cankaya")
                + ","
                + addressBlock("Izmir", "Karsiyaka")
                + "]"
                + "},"
                + "\"session\":{"
                + "\"id\":\"sess-" + i + "\","
                + "\"ip\":\"127.0.0.1\","
                + "\"device\":\"chrome\","
                + "\"history\":["
                + historyBlock(30)
                + "]"
                + "},"
                + "\"orders\":[" + ordersBlock(100) + "],"
                + "\"logs\":[" + logsBlock(150) + "]"
                + "}";
    }
    private static String addressBlock(String city, String district) {
        return "{"
                + "\"city\":\"" + city + "\","
                + "\"district\":\"" + district + "\","
                + "\"geo\":{"
                + "\"lat\":40.0,"
                + "\"lon\":29.0"
                + "}"
                + "}";
    }
    private static String historyBlock(int count1) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count1; i++) {
            sb.append("{\"event\":\"click\",\"page\":\"page-")
                    .append(i)
                    .append("\",\"ts\":")
                    .append(System.nanoTime())
                    .append("}");
            if (i < count1 - 1) sb.append(",");
        }
        return sb.toString();
    }

    private static String ordersBlock(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append("{\"id\":")
                    .append(i)
                    .append(",\"price\":")
                    .append(i * 9.99)
                    .append(",\"items\":")
                    .append(i % 5 + 1)
                    .append("}");
            if (i < count - 1) sb.append(",");
        }
        return sb.toString();
    }

    private static String logsBlock(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append("{\"level\":\"INFO\",\"msg\":\"log-")
                    .append(i)
                    .append("-")
                    .append("x".repeat(10))
                    .append("\",\"time\":")
                    .append(System.currentTimeMillis())
                    .append("}");
            if (i < count - 1) sb.append(",");
        }
        return sb.toString();
    }
}
