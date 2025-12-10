package com.empresa.course.MicroserviceCourseApi.Service.Inter;

import com.empresa.course.MicroserviceCourseApi.Entities.Course;
import com.empresa.course.MicroserviceCourseApi.Entities.Enums.CourseType;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para la gestión de cursos (Single Responsibility)
 */
public interface ICourseService {

    Course create(Course course);

    Course update(Long id, Course course);

    Course findById(Long id);

    Optional<Course> findByIdOptional(Long id);

    List<Course> findAll();

    List<Course> findByType(CourseType courseType);

    List<Course> findActive();

    void delete(Long id);

    Course addStudent(Long courseId, Long studentId);

    Course removeStudent(Long courseId, Long studentId);

    Course assignTeacher(Long courseId, Long teacherId);

    int getStudentCount(Long courseId);
}

