package com.empresa.students.MicroserviceStudentsAPI.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@Table(name = "students")
@AllArgsConstructor
@NoArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String Name;

    @Column(name = "last_name")
    private String lastName;
    private boolean Socio;
    private String Email;

    @Column(name = "tlf_contacto_1")
    private int Phone1;

    @Column(name = "tlf_contacto_2")
    private int Phone2;
    private String CurseLevel;

    @Column(name = "id_course")
    private Long IdCourse;
}
