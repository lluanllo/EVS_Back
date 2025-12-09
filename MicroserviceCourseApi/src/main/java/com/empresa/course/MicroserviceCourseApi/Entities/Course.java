package com.empresa.course.MicroserviceCourseApi.Entities;

import com.empresa.course.MicroserviceCourseApi.Entities.Enums.CourseType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Builder
@Table(name = "courses")
@AllArgsConstructor
@NoArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    private String status;

    @Enumerated(EnumType.STRING)
    private Turno turno;

    // Tipo de curso (windsurf, catamaran, etc)
    @Enumerated(EnumType.STRING)
    @Column(name = "course_type")
    private CourseType courseType;

    // Duración del curso en minutos
    @Column(name = "duration_minutes")
    private Integer durationMinutes = 60;

    // Número máximo de estudiantes
    @Column(name = "max_students")
    private Integer maxStudents = 10;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    // IDs de profesores asignados
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "course_teachers", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "teacher_id")
    private Set<Long> teacherIds = new HashSet<>();

    // IDs de estudiantes inscritos
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "course_students", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "student_id")
    private Set<Long> studentIds = new HashSet<>();

    // Horarios del curso
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Schedule> schedules = new ArrayList<>();

    // Precio del curso
    private Double price;

    // Si el curso está activo
    private Boolean active = true;
}
