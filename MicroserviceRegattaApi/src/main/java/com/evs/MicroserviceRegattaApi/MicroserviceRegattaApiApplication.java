package com.evs.MicroserviceRegattaApi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class MicroserviceRegattaApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceRegattaApiApplication.class, args);
	}

}
