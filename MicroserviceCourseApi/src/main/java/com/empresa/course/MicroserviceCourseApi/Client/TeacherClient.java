package com.empresa.course.MicroserviceCourseApi.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Cliente Feign para comunicarse con el microservicio de Teachers
 */
@FeignClient(name = "msvc-teacher-api", path = "/api/teachers")
public interface TeacherClient {

    @GetMapping("/{id}")
    Object getTeacherById(@PathVariable("id") Long id);

    @GetMapping("/{id}/exists")
    Boolean existsById(@PathVariable("id") Long id);
}

