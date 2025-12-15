package com.empresa.eureka.MicroserviceErekaServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class MicroserviceErekaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceErekaServerApplication.class, args);
	}

}
