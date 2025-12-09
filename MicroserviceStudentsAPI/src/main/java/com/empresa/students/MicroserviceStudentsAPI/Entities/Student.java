package com.empresa.students.MicroserviceStudentsAPI.Entities;

import com.empresa.students.MicroserviceStudentsAPI.Entities.Enums.SkillLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Builder
@Table(name = "students")
@AllArgsConstructor
@NoArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "last_name")
    private String lastName;

    @Column(unique = true)
    private String dni;

    @Column(unique = true)
    private String email;

    private String password;

    private Boolean socio = false;

    @Column(name = "tlf_contacto_1")
    private String phone1;

    @Column(name = "tlf_contacto_2")
    private String phone2;

    // Nivel de habilidad del estudiante
    @Enumerated(EnumType.STRING)
    @Column(name = "skill_level")
    private SkillLevel skillLevel = SkillLevel.BEGINNER;

    // Fecha de nacimiento para campamentos de verano
    @Column(name = "birth_date")
    private LocalDate birthDate;

    // IDs de los cursos en los que está inscrito
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "student_courses", joinColumns = @JoinColumn(name = "student_id"))
    @Column(name = "course_id")
    private Set<Long> courseIds = new HashSet<>();

    // Notas o comentarios sobre el estudiante
    @Column(length = 1000)
    private String notes;

    // Contacto de emergencia
    @Column(name = "emergency_contact")
    private String emergencyContact;

    @Column(name = "emergency_phone")
    private String emergencyPhone;
}
