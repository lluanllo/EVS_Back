package com.empresa.course.MicroserviceCourseApi.Client;

import com.empresa.course.MicroserviceCourseApi.Controller.DTO.StudentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "students-api", url = "http://localhost:8081/api/students")
public interface StudentClient {

    @GetMapping("/{id}")
    StudentDTO getStudentById(@PathVariable Long id);

    @GetMapping
    List<StudentDTO> getAllStudents();
}
