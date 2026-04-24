package com.benchnark.poc_distributed_systems.config;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;
import java.util.List;

@Profile("external")
@Configuration
public class HazalCastClientConfig {

    @Bean
    public HazelcastInstance hazelcastClient() {

        ClientConfig config = new ClientConfig();

        config.setClusterName("dev");

        config.getNetworkConfig().setConnectionTimeout(5000);

        config.getNetworkConfig().setAddresses(
                List.of(
                        "localhost:5701",
                        "localhost:5702",
                        "localhost:5703"
                )
        );

        return HazelcastClient.newHazelcastClient(config);
    }
}