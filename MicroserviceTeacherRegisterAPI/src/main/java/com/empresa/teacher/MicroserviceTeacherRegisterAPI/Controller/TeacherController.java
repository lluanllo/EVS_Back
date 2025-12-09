package com.empresa.teacher.MicroserviceTeacherRegisterAPI.Controller;

import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Teacher;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Service.ITeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/Teacher")
public class TeacherController {

    @Autowired
    private ITeacherService teacherService;

    @RequestMapping("/save")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveTeacher(@RequestBody Teacher teacher) {
        teacherService.save(teacher);
    }

    @PutMapping("/update/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateTeacherById(@PathVariable Long id, @RequestBody Teacher teacher) {
        teacherService.updateById(id, teacher);
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return ResponseEntity.ok(teacherService.findById(id));
    }

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(teacherService.findAll());
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTeacher(@PathVariable Long id) {
        teacherService.delete(id);
    }

    @GetMapping("/findByIdCourse/{id}")
    public ResponseEntity<?> findByIdCourse(@PathVariable Long idCourse) {
        return ResponseEntity.ok(teacherService.findByIdCourse(idCourse));
    }
}
