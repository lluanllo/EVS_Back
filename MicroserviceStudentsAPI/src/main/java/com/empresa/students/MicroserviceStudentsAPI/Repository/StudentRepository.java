package com.empresa.students.MicroserviceStudentsAPI.Repository;

import com.empresa.students.MicroserviceStudentsAPI.Entities.Student;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends CrudRepository<Student, Long> {

    List<Student> findByIdCourse(Long CourseId);
}
