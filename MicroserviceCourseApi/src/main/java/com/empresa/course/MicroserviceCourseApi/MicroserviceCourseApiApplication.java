package com.empresa.course.MicroserviceCourseApi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableDiscoveryClient
@EnableScheduling
@SpringBootApplication
public class MicroserviceCourseApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceCourseApiApplication.class, args);
	}

}
