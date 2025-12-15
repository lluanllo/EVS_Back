package com.empresa.course.MicroserviceCourseApi.Service.Impl;

import com.empresa.course.MicroserviceCourseApi.Entities.Course;
import com.empresa.course.MicroserviceCourseApi.HTTP.Response.StudentsTeachersByCourseResponse;
import com.empresa.course.MicroserviceCourseApi.Repository.CourseRepository;
import com.empresa.course.MicroserviceCourseApi.Service.Inter.ICourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImpl implements ICourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public void save(Course course) {
        courseRepository.save(course);
    }

    @Override
    public List<Course> findAll() {
        return (List<Course>) courseRepository.findAll();
    }

    @Override
    public Course findById(Long id) {
        return courseRepository.findById(id).orElseThrow();
    }

    @Override
    public Course DeleteById(Long id) {
        Course course = courseRepository.findById(id).orElseThrow();
        courseRepository.deleteById(id);
        return course;
    }

    public StudentsTeachersByCourseResponse getStudentsAndTeachersByCourse(Long idCourse) {

        Course course = courseRepository.findById(idCourse).orElse(new Course());

        // TODO: Use Kafka to get students and teachers
        return StudentsTeachersByCourseResponse.builder()
                .CourseName(course.getName())
                .teachersDTOList(List.of()) // Stub
                .studentsDTOList(List.of()) // Stub
                .build();
    }

    @Override
    public int getStudentCount(Long courseId) {
        // TODO: Use Kafka to get count
        return 0; // Stub
    }
}