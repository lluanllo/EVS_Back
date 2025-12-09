package com.empresa.teacher.MicroserviceTeacherRegisterAPI.Service;

import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Teacher;

import java.util.List;

public interface ITeacherService {

    public List<Teacher> findAll();
    public Teacher findById(Long id);
    public Teacher save(Teacher student);
    public Teacher updateById(Long id, Teacher student);
    public void delete(Long id);
    public List<Teacher> findByIdCourse(Long idCourse);
}