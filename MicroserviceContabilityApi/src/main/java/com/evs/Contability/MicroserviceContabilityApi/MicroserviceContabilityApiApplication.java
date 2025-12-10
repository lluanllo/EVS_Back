package com.evs.Contability.MicroserviceContabilityApi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class MicroserviceContabilityApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceContabilityApiApplication.class, args);
	}

}
