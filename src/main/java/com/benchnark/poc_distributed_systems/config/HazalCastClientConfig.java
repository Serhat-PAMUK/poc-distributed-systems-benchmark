package com.benchnark.poc_distributed_systems.config;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Value; // @Value için
import java.util.Arrays; // Arrays.asList için
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;





@Profile("external")
@Configuration
public class HazalCastClientConfig {

    @Value("${hazelcast_servers}")
    private String hazelcastServers;

    @Bean
    public HazelcastInstance hazelcastClient() {

        ClientConfig config = new ClientConfig();

        config.setClusterName("dev");

        config.getNetworkConfig().setConnectionTimeout(5000);

        List<String> addresses = Arrays.asList(hazelcastServers.split(","));
        config.getNetworkConfig().setAddresses(addresses);


        return HazelcastClient.newHazelcastClient(config);
    }
}