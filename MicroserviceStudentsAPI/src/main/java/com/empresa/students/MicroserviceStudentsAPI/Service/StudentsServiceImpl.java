package com.empresa.students.MicroserviceStudentsAPI.Service;

import com.empresa.students.MicroserviceStudentsAPI.Entities.Student;
import com.empresa.students.MicroserviceStudentsAPI.Repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentsServiceImpl implements IStudentService {

    @Autowired
    private StudentRepository studentsRepository;

    @Override
    public List<Student> findAll() {
        return (List<Student>) studentsRepository.findAll();
    }

    @Override
    public Student findById(Long id) {
        return studentsRepository.findById(id).orElseThrow();
    }

    @Override
    public Student save(Student student) {
        return studentsRepository.save(student);
    }

    @Override
    public Student updateById(Long id, Student student) {
        return studentsRepository.save(student);
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public List<Student> findByIdCourse(Long idCourse) {
        return studentsRepository.findByIdCourse(idCourse);
    }
}
