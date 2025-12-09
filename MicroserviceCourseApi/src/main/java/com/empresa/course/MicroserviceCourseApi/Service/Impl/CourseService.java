package com.empresa.course.MicroserviceCourseApi.Service.Impl;

import com.empresa.course.MicroserviceCourseApi.Controller.DTO.CourseRequest;
import com.empresa.course.MicroserviceCourseApi.Controller.DTO.CourseResponse;
import com.empresa.course.MicroserviceCourseApi.Entities.Course;
import com.empresa.course.MicroserviceCourseApi.Entities.Enums.CourseType;
import com.empresa.course.MicroserviceCourseApi.Entities.Turno;
import com.empresa.course.MicroserviceCourseApi.Kafka.Events.CourseEvent;
import com.empresa.course.MicroserviceCourseApi.Kafka.Producer.CourseEventProducer;
import com.empresa.course.MicroserviceCourseApi.Repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseEventProducer eventProducer;

    public List<CourseResponse> findAll() {
        return courseRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CourseResponse findById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado: " + id));
        return toResponse(course);
    }

    @Transactional
    public CourseResponse create(CourseRequest request) {
        Course course = Course.builder()
                .name(request.getName())
                .description(request.getDescription())
                .status("ACTIVE")
                .turno(request.getTurno())
                .courseType(request.getCourseType())
                .durationMinutes(request.getDurationMinutes() != null ? request.getDurationMinutes() : 60)
                .maxStudents(request.getMaxStudents() != null ? request.getMaxStudents() : 10)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .teacherIds(request.getTeacherIds() != null ? request.getTeacherIds() : new HashSet<>())
                .studentIds(request.getStudentIds() != null ? request.getStudentIds() : new HashSet<>())
                .price(request.getPrice())
                .active(true)
                .build();

        course = courseRepository.save(course);

        // Publicar evento
        publishCourseEvent(course, "CREATED");

        return toResponse(course);
    }

    @Transactional
    public CourseResponse update(Long id, CourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado: " + id));

        course.setName(request.getName());
        course.setDescription(request.getDescription());
        course.setTurno(request.getTurno());
        course.setCourseType(request.getCourseType());
        if (request.getDurationMinutes() != null) {
            course.setDurationMinutes(request.getDurationMinutes());
        }
        if (request.getMaxStudents() != null) {
            course.setMaxStudents(request.getMaxStudents());
        }
        course.setStartDate(request.getStartDate());
        course.setEndDate(request.getEndDate());
        course.setStartTime(request.getStartTime());
        course.setEndTime(request.getEndTime());
        course.setPrice(request.getPrice());

        course = courseRepository.save(course);

        publishCourseEvent(course, "UPDATED");

        return toResponse(course);
    }

    @Transactional
    public void delete(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado: " + id));

        courseRepository.deleteById(id);
        publishCourseEvent(course, "DELETED");
    }

    public List<CourseResponse> findByCourseType(CourseType courseType) {
        return courseRepository.findByCourseType(courseType).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<CourseResponse> findByTurno(Turno turno) {
        return courseRepository.findByTurno(turno).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<CourseResponse> findActive() {
        return courseRepository.findByActiveTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<CourseResponse> findByTeacherId(Long teacherId) {
        return courseRepository.findByTeacherId(teacherId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<CourseResponse> findByStudentId(Long studentId) {
        return courseRepository.findByStudentId(studentId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void assignTeacher(Long courseId, Long teacherId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado: " + courseId));

        course.getTeacherIds().add(teacherId);
        courseRepository.save(course);

        CourseEvent event = CourseEvent.builder()
                .eventType("TEACHER_ASSIGNED")
                .timestamp(LocalDateTime.now())
                .courseId(courseId)
                .courseName(course.getName())
                .courseType(course.getCourseType().name())
                .teacherId(teacherId)
                .build();
        eventProducer.publishCourseEvent(event);
    }

    @Transactional
    public void removeTeacher(Long courseId, Long teacherId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado: " + courseId));

        course.getTeacherIds().remove(teacherId);
        courseRepository.save(course);
    }

    @Transactional
    public void enrollStudent(Long courseId, Long studentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado: " + courseId));

        if (course.getMaxStudents() != null && course.getStudentIds().size() >= course.getMaxStudents()) {
            throw new RuntimeException("El curso está lleno");
        }

        course.getStudentIds().add(studentId);
        courseRepository.save(course);

        CourseEvent event = CourseEvent.builder()
                .eventType("STUDENT_ENROLLED")
                .timestamp(LocalDateTime.now())
                .courseId(courseId)
                .courseName(course.getName())
                .courseType(course.getCourseType().name())
                .studentId(studentId)
                .build();
        eventProducer.publishCourseEvent(event);
    }

    @Transactional
    public void unenrollStudent(Long courseId, Long studentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado: " + courseId));

        course.getStudentIds().remove(studentId);
        courseRepository.save(course);
    }

    @Transactional
    public void activateCourse(Long courseId, boolean active) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado: " + courseId));

        course.setActive(active);
        course.setStatus(active ? "ACTIVE" : "INACTIVE");
        courseRepository.save(course);
    }

    private void publishCourseEvent(Course course, String eventType) {
        CourseEvent event = CourseEvent.builder()
                .eventType(eventType)
                .timestamp(LocalDateTime.now())
                .courseId(course.getId())
                .courseName(course.getName())
                .courseType(course.getCourseType() != null ? course.getCourseType().name() : null)
                .status(course.getStatus())
                .build();
        eventProducer.publishCourseEvent(event);
    }

    private CourseResponse toResponse(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .status(course.getStatus())
                .turno(course.getTurno())
                .courseType(course.getCourseType())
                .durationMinutes(course.getDurationMinutes())
                .maxStudents(course.getMaxStudents())
                .startDate(course.getStartDate())
                .endDate(course.getEndDate())
                .startTime(course.getStartTime())
                .endTime(course.getEndTime())
                .teacherIds(course.getTeacherIds())
                .studentIds(course.getStudentIds())
                .price(course.getPrice())
                .active(course.getActive())
                .currentStudents(course.getStudentIds() != null ? course.getStudentIds().size() : 0)
                .build();
    }
}

