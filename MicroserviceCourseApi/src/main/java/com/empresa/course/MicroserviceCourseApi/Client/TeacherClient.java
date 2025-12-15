package com.empresa.course.MicroserviceCourseApi.Client;

import com.empresa.course.MicroserviceCourseApi.Controller.DTO.TeacherDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "teachers-api", url = "http://localhost:8082/api/teachers")
public interface TeacherClient {

    @GetMapping("/{id}")
    TeacherDTO getTeacherById(@PathVariable Long id);

    @GetMapping
    List<TeacherDTO> getAllTeachers();

    @GetMapping("/course/{courseId}")
    List<TeacherDTO> findAllTeachersByCourse(@PathVariable Long courseId);
}
