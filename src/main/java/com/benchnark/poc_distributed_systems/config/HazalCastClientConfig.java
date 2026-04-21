package com.benchnark.poc_distributed_systems.config;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("external")
@Configuration
public class HazalCastClientConfig {

    @Bean
    public HazelcastInstance hazelcastClient(
            @Value("${hazelcast.servers:127.0.0.1:5701,127.0.0.1:5702}") String servers) {

        ClientConfig config = new ClientConfig();

        String[] serverArray = servers.split(",");
        config.getNetworkConfig().addAddress(serverArray);


        config.getNetworkConfig().setConnectionTimeout(5000);


        config.setInstanceName("spring-hazelcast-client");

        return HazelcastClient.newHazelcastClient(config);
    }
}