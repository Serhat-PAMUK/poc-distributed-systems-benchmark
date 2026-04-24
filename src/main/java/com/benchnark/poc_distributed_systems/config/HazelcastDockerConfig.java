package com.benchnark.poc_distributed_systems.config;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("docker")
@Configuration
public class HazelcastDockerConfig {

    @Bean
    public HazelcastInstance hazelcastInstance() {
        Config config = new Config();
        config.setClusterName("dev");


        config.getNetworkConfig()
                .getJoin()
                .getTcpIpConfig()
                .setEnabled(true);

        return Hazelcast.newHazelcastInstance(config);
    }
}
