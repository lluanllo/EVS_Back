package com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities;

import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Enums.ContractType;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Enums.Role;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Enums.Speciality;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Builder
@Table(name = "teachers")
@AllArgsConstructor
@NoArgsConstructor
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "teacher_name", nullable = false)
    private String name;

    @Column(name = "last_name")
    private String lastName;

    @Column(unique = true)
    private String dni;

    @Column(name = "phone_number")
    private String phone;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    // Especialidades del profesor (puede tener varias)
    @ElementCollection(targetClass = Speciality.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "teacher_specialities", joinColumns = @JoinColumn(name = "teacher_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "speciality")
    private Set<Speciality> specialities = new HashSet<>();

    // Tipo de contrato con prioridad
    @Enumerated(EnumType.STRING)
    @Column(name = "contract_type", nullable = false)
    private ContractType contractType;

    // Rol para autenticación
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.TEACHER;

    // Horas trabajadas en el periodo actual
    @Column(name = "worked_hours")
    private Integer workedHours = 0;

    // Horas máximas por semana según contrato
    @Column(name = "max_weekly_hours")
    private Integer maxWeeklyHours = 40;

    // Disponibilidad actual
    @Column(name = "available")
    private Boolean available = true;

    // Si ha confirmado disponibilidad para el próximo horario
    @Column(name = "confirmed_availability")
    private Boolean confirmedAvailability = false;

    @Column(name = "id_course")
    private Long idCourse;
}