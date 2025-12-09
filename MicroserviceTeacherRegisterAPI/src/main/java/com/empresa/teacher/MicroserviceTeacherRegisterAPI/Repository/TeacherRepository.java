package com.empresa.teacher.MicroserviceTeacherRegisterAPI.Repository;

import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Teacher;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherRepository extends CrudRepository<Teacher, Long> {
    List<Teacher> findByIdCourse(Long CourseId);
}
