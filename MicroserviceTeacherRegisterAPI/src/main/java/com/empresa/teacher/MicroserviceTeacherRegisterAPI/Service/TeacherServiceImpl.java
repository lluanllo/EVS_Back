package com.empresa.teacher.MicroserviceTeacherRegisterAPI.Service;

import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Teacher;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherServiceImpl implements ITeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Override
    public List<Teacher> findAll() {
        return (List<Teacher>) teacherRepository.findAll();
    }

    @Override
    public Teacher findById(Long id) {
        return teacherRepository.findById(id).orElseThrow();
    }

    @Override
    public Teacher save(Teacher student) {
        return teacherRepository.save(student);
    }

    @Override
    public Teacher updateById(Long id, Teacher Teacher) {
        return teacherRepository.save(Teacher);
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public List<Teacher> findByIdCourse(Long idCourse) {
        return teacherRepository.findByIdCourse(idCourse);
    }
}