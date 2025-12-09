package com.empresa.students.MicroserviceStudentsAPI.Service;

import com.empresa.students.MicroserviceStudentsAPI.Entities.Student;

import java.util.List;

public interface IStudentService {
    public List<Student> findAll();
    public Student findById(Long id);
    public Student save(Student student);
    public Student updateById(Long id, Student student);
    public void delete(Long id);
    public List<Student> findByIdCourse(Long idCourse);
}
