package com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@Table(name = "teachers")
@AllArgsConstructor
@NoArgsConstructor
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(name = "teacher_name")
    private String name;
    private String lastName;
    private String dni;

    @Column(name = "phone_number")
    private String phone;
    private String email;
    private String speciality;

    @Column(name = "id_course")
    private Long IdCourse;
}