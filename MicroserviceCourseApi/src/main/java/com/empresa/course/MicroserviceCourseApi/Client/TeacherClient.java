package com.empresa.course.MicroserviceCourseApi.Client;

import com.empresa.course.MicroserviceCourseApi.Controller.DTO.TeacherDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "msvc-teacher-api", url = "http://localhost:8082/api/Teacher/")
public interface TeacherClient {

    @GetMapping("/findByIdCourse/{id}")
    List<TeacherDTO> findAllTeachersByCourse(@PathVariable Long IdCourse);
}