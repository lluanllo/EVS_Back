package com.empresa.course.MicroserviceCourseApi.Controller;

import com.empresa.course.MicroserviceCourseApi.Controller.DTO.CourseRequest;
import com.empresa.course.MicroserviceCourseApi.Controller.DTO.CourseResponse;
import com.empresa.course.MicroserviceCourseApi.Controller.DTO.RoutePlanRequest;
import com.empresa.course.MicroserviceCourseApi.Controller.DTO.RoutePlanResponse;
import com.empresa.course.MicroserviceCourseApi.Entities.Enums.CourseType;
import com.empresa.course.MicroserviceCourseApi.Entities.RoutePlan;
import com.empresa.course.MicroserviceCourseApi.Entities.Turno;
import com.empresa.course.MicroserviceCourseApi.Service.Impl.CourseService;
import com.empresa.course.MicroserviceCourseApi.Service.Impl.RoutePlannerService;
import com.empresa.course.MicroserviceCourseApi.Service.Impl.ScheduleOrganizerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final RoutePlannerService routePlannerService;
    private final ScheduleOrganizerService scheduleOrganizerService;

    // ==================== CRUD de Cursos ====================

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<List<CourseResponse>> findAll() {
        return ResponseEntity.ok(courseService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<CourseResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseResponse> create(@Valid @RequestBody CourseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody CourseRequest request) {
        return ResponseEntity.ok(courseService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== Búsquedas ====================

    @GetMapping("/type/{courseType}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<CourseResponse>> findByCourseType(@PathVariable CourseType courseType) {
        return ResponseEntity.ok(courseService.findByCourseType(courseType));
    }

    @GetMapping("/turno/{turno}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<CourseResponse>> findByTurno(@PathVariable Turno turno) {
        return ResponseEntity.ok(courseService.findByTurno(turno));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<CourseResponse>> findActive() {
        return ResponseEntity.ok(courseService.findActive());
    }

    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<CourseResponse>> findByTeacherId(@PathVariable Long teacherId) {
        return ResponseEntity.ok(courseService.findByTeacherId(teacherId));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<List<CourseResponse>> findByStudentId(@PathVariable Long studentId) {
        return ResponseEntity.ok(courseService.findByStudentId(studentId));
    }

    // ==================== Gestión de profesores y estudiantes ====================

    @PostMapping("/{courseId}/assign-teacher/{teacherId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> assignTeacher(@PathVariable Long courseId,
                                               @PathVariable Long teacherId) {
        courseService.assignTeacher(courseId, teacherId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{courseId}/remove-teacher/{teacherId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeTeacher(@PathVariable Long courseId,
                                               @PathVariable Long teacherId) {
        courseService.removeTeacher(courseId, teacherId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{courseId}/enroll/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Void> enrollStudent(@PathVariable Long courseId,
                                               @PathVariable Long studentId) {
        courseService.enrollStudent(courseId, studentId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{courseId}/unenroll/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Void> unenrollStudent(@PathVariable Long courseId,
                                                 @PathVariable Long studentId) {
        courseService.unenrollStudent(courseId, studentId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{courseId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateCourse(@PathVariable Long courseId,
                                                @RequestParam boolean active) {
        courseService.activateCourse(courseId, active);
        return ResponseEntity.ok().build();
    }

    // ==================== Planificador de rutas ====================

    @PostMapping("/generate-route")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<RoutePlanResponse> generateRoute(@Valid @RequestBody RoutePlanRequest request) {
        RoutePlan plan = routePlannerService.generateRoutePlan(
                request.getCourseId(),
                request.getCourseType(),
                request.getWindDirection(),
                request.getWindSpeedKnots(),
                request.getClassDurationMinutes(),
                request.getStudentLevel()
        );
        return ResponseEntity.ok(toRoutePlanResponse(plan));
    }

    @PostMapping("/{courseId}/generate-route")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<RoutePlanResponse> generateRouteForCourse(
            @PathVariable Long courseId,
            @Valid @RequestBody RoutePlanRequest request) {
        request.setCourseId(courseId);
        RoutePlan plan = routePlannerService.generateRoutePlan(
                courseId,
                request.getCourseType(),
                request.getWindDirection(),
                request.getWindSpeedKnots(),
                request.getClassDurationMinutes(),
                request.getStudentLevel()
        );
        return ResponseEntity.ok(toRoutePlanResponse(plan));
    }

    // ==================== Organizador de horarios ====================

    @PostMapping("/organize-week")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> organizeWeekSchedule(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStartDate) {
        var schedules = scheduleOrganizerService.organizeWeekSchedule(weekStartDate);
        return ResponseEntity.ok(schedules);
    }

    @PostMapping("/reorganize-day")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> reorganizeDaySchedule(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        var schedules = scheduleOrganizerService.reorganizeDaySchedule(date);
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/available-slots")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<?> getAvailableSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Turno turno) {
        var slots = scheduleOrganizerService.getAvailableSlots(date, turno);
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/occupancy-stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getWeekOccupancyStats() {
        return ResponseEntity.ok(scheduleOrganizerService.getWeekOccupancyStats());
    }

    // ==================== Helpers ====================

    private RoutePlanResponse toRoutePlanResponse(RoutePlan plan) {
        return RoutePlanResponse.builder()
                .id(plan.getId())
                .courseId(plan.getCourseId())
                .courseType(plan.getCourseType())
                .windDirection(plan.getWindDirection())
                .windSpeedKnots(plan.getWindSpeedKnots())
                .classDurationMinutes(plan.getClassDurationMinutes())
                .studentLevel(plan.getStudentLevel())
                .createdAt(plan.getCreatedAt())
                .summary(plan.getSummary())
                .safetyNotes(plan.getSafetyNotes())
                .imageBase64(plan.getImageBase64())
                .legs(plan.getLegs().stream()
                        .map(leg -> RoutePlanResponse.RouteLegResponse.builder()
                                .order(leg.getOrder())
                                .maneuverType(leg.getManeuverType())
                                .heading(leg.getHeading())
                                .durationMinutes(leg.getDurationMinutes())
                                .distanceMeters(leg.getDistanceMeters())
                                .description(leg.getDescription())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
