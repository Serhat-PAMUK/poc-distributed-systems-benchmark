package com.benchnark.poc_distributed_systems;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.Cacheable;

@SpringBootApplication
public class PocDistributedSystemsApplication {

	public static void main(String[] args) {
		SpringApplication.run(PocDistributedSystemsApplication.class, args);
	}

}
