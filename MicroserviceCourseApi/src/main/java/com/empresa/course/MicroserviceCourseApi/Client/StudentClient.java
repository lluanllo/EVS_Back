package com.empresa.course.MicroserviceCourseApi.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Cliente Feign para comunicarse con el microservicio de Students
 */
@FeignClient(name = "msvc-students-api", path = "/api/students")
public interface StudentClient {

    @GetMapping("/{id}")
    Object getStudentById(@PathVariable("id") Long id);

    @GetMapping("/{id}/exists")
    Boolean existsById(@PathVariable("id") Long id);
}

