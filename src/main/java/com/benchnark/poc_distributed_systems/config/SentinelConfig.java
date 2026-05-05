 package com.benchnark.poc_distributed_systems.config;

import io.lettuce.core.ClientOptions;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;

public class SentinelConfig {
    @Bean
    public LettuceClientConfigurationBuilderCustomizer lettuceCustomizer() {
        return clientConfigurationBuilder -> clientConfigurationBuilder
                .clientOptions(ClientOptions.builder()
                        // Sentinel modunda bağlantı hatalarını daha hızlı fark etmek için
                        .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                        .autoReconnect(true)
                        .build());
    }
}