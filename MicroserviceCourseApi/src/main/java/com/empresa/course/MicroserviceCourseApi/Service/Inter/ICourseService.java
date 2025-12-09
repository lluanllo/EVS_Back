package com.empresa.course.MicroserviceCourseApi.Service.Inter;

import com.empresa.course.MicroserviceCourseApi.Entities.Course;
import com.empresa.course.MicroserviceCourseApi.HTTP.Response.StudentsTeachersByCourseResponse;

import java.util.List;

public interface ICourseService {

    void save(Course course);
    List<Course> findAll();
    Course findById(Long id);
    Course DeleteById(Long id);
    StudentsTeachersByCourseResponse getStudentsAndTeachersByCourse(Long idCourse);
}
