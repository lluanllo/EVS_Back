package com.empresa.course.MicroserviceCourseApi.Service.Impl;

import com.empresa.course.MicroserviceCourseApi.Entities.Course;
import com.empresa.course.MicroserviceCourseApi.Entities.Enums.CourseType;
import com.empresa.course.MicroserviceCourseApi.Entities.Schedule;
import com.empresa.course.MicroserviceCourseApi.Entities.Turno;
import com.empresa.course.MicroserviceCourseApi.Repository.CourseRepository;
import com.empresa.course.MicroserviceCourseApi.Repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio para organizar los horarios de clases.
 * Implementa un algoritmo que:
 * 1. Distribuye las clases a lo largo del día según turno
 * 2. Evita solapamientos
 * 3. Optimiza el uso de recursos (profesores y material)
 * 4. Agrupa clases del mismo tipo cuando es posible
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleOrganizerService {

    private final CourseRepository courseRepository;
    private final ScheduleRepository scheduleRepository;

    // Horarios por turno
    private static final LocalTime MORNING_START = LocalTime.of(9, 0);
    private static final LocalTime MORNING_END = LocalTime.of(14, 0);
    private static final LocalTime AFTERNOON_START = LocalTime.of(16, 0);
    private static final LocalTime AFTERNOON_END = LocalTime.of(20, 0);

    // Tiempo mínimo entre clases (minutos)
    private static final int MIN_GAP_BETWEEN_CLASSES = 15;

    /**
     * Organiza automáticamente los horarios para una semana.
     *
     * @param weekStartDate Fecha de inicio de la semana
     * @return Lista de horarios generados
     */
    @Transactional
    public List<Schedule> organizeWeekSchedule(LocalDate weekStartDate) {
        log.info("Organizando horarios para la semana del {}", weekStartDate);

        List<Schedule> generatedSchedules = new ArrayList<>();

        // Obtener todos los cursos activos
        List<Course> activeCourses = courseRepository.findByActiveTrue();

        if (activeCourses.isEmpty()) {
            log.warn("No hay cursos activos para programar");
            return generatedSchedules;
        }

        // Organizar por cada día de la semana (L-V para cursos normales, L-D para summer camp)
        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = weekStartDate.plusDays(i);
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();

            // Filtrar cursos que aplican para este día
            List<Course> coursesForDay = filterCoursesForDay(activeCourses, dayOfWeek);

            // Organizar turno de mañana
            List<Schedule> morningSchedules = organizeTimeSlot(
                    coursesForDay.stream().filter(c -> c.getTurno() == Turno.MANANA).collect(Collectors.toList()),
                    dayOfWeek, MORNING_START, MORNING_END);
            generatedSchedules.addAll(morningSchedules);

            // Organizar turno de tarde
            List<Schedule> afternoonSchedules = organizeTimeSlot(
                    coursesForDay.stream().filter(c -> c.getTurno() == Turno.TARDE).collect(Collectors.toList()),
                    dayOfWeek, AFTERNOON_START, AFTERNOON_END);
            generatedSchedules.addAll(afternoonSchedules);
        }

        // Guardar todos los horarios
        generatedSchedules = scheduleRepository.saveAll(generatedSchedules);

        log.info("Se han generado {} horarios para la semana", generatedSchedules.size());
        return generatedSchedules;
    }

    /**
     * Filtra los cursos que aplican para un día específico.
     */
    private List<Course> filterCoursesForDay(List<Course> courses, DayOfWeek dayOfWeek) {
        return courses.stream()
                .filter(c -> {
                    // Summer camp todos los días
                    if (c.getCourseType() == CourseType.SUMMER_CAMP) {
                        return true;
                    }
                    // Otros cursos solo L-V
                    return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
                })
                .collect(Collectors.toList());
    }

    /**
     * Organiza las clases dentro de un slot de tiempo.
     */
    private List<Schedule> organizeTimeSlot(List<Course> courses, DayOfWeek dayOfWeek,
                                             LocalTime slotStart, LocalTime slotEnd) {
        List<Schedule> schedules = new ArrayList<>();

        if (courses.isEmpty()) {
            return schedules;
        }

        // Ordenar cursos por prioridad (más estudiantes primero, luego por tipo)
        courses.sort((c1, c2) -> {
            int studentsCompare = Integer.compare(
                    c2.getStudentIds().size(),
                    c1.getStudentIds().size()
            );
            if (studentsCompare != 0) return studentsCompare;
            return c1.getCourseType().compareTo(c2.getCourseType());
        });

        // Track de tiempo disponible
        LocalTime currentTime = slotStart;

        for (Course course : courses) {
            int durationMinutes = course.getDurationMinutes() != null ? course.getDurationMinutes() : 60;

            // Verificar si cabe en el slot
            LocalTime endTime = currentTime.plusMinutes(durationMinutes);
            if (endTime.isAfter(slotEnd)) {
                log.warn("No hay espacio para {} en el turno", course.getName());
                continue;
            }

            // Crear horario
            Schedule schedule = Schedule.builder()
                    .course(course)
                    .dayOfWeek(dayOfWeek)
                    .startTime(currentTime)
                    .endTime(endTime)
                    .confirmed(false)
                    .notes(generateScheduleNotes(course))
                    .build();

            schedules.add(schedule);

            // Avanzar tiempo
            currentTime = endTime.plusMinutes(MIN_GAP_BETWEEN_CLASSES);
        }

        return schedules;
    }

    /**
     * Reorganiza el horario de un día específico optimizando recursos.
     */
    @Transactional
    public List<Schedule> reorganizeDaySchedule(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        // Eliminar horarios existentes para ese día
        List<Schedule> existing = scheduleRepository.findByDayOfWeek(dayOfWeek);
        scheduleRepository.deleteAll(existing);

        // Obtener cursos activos
        List<Course> activeCourses = courseRepository.findByActiveTrue();
        List<Course> coursesForDay = filterCoursesForDay(activeCourses, dayOfWeek);

        List<Schedule> newSchedules = new ArrayList<>();

        // Mañana
        newSchedules.addAll(organizeTimeSlot(
                coursesForDay.stream().filter(c -> c.getTurno() == Turno.MANANA).collect(Collectors.toList()),
                dayOfWeek, MORNING_START, MORNING_END));

        // Tarde
        newSchedules.addAll(organizeTimeSlot(
                coursesForDay.stream().filter(c -> c.getTurno() == Turno.TARDE).collect(Collectors.toList()),
                dayOfWeek, AFTERNOON_START, AFTERNOON_END));

        return scheduleRepository.saveAll(newSchedules);
    }

    /**
     * Obtiene la disponibilidad de slots para un día.
     */
    public List<TimeSlot> getAvailableSlots(LocalDate date, Turno turno) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        List<Schedule> existingSchedules = scheduleRepository.findByDayOfWeek(dayOfWeek);

        LocalTime slotStart = turno == Turno.MANANA ? MORNING_START : AFTERNOON_START;
        LocalTime slotEnd = turno == Turno.MANANA ? MORNING_END : AFTERNOON_END;

        // Ordenar horarios existentes
        existingSchedules.sort(Comparator.comparing(Schedule::getStartTime));

        List<TimeSlot> availableSlots = new ArrayList<>();
        LocalTime currentStart = slotStart;

        for (Schedule schedule : existingSchedules) {
            if (schedule.getStartTime().isAfter(currentStart)) {
                // Hay un hueco
                availableSlots.add(new TimeSlot(currentStart, schedule.getStartTime()));
            }
            currentStart = schedule.getEndTime().plusMinutes(MIN_GAP_BETWEEN_CLASSES);
        }

        // Slot final si queda tiempo
        if (currentStart.isBefore(slotEnd)) {
            availableSlots.add(new TimeSlot(currentStart, slotEnd));
        }

        return availableSlots;
    }

    /**
     * Verifica si hay conflictos de horario para un profesor.
     */
    public boolean hasScheduleConflict(Long teacherId, DayOfWeek dayOfWeek,
                                        LocalTime startTime, LocalTime endTime) {
        List<Schedule> teacherSchedules = scheduleRepository.findByTeacherId(teacherId);

        return teacherSchedules.stream()
                .filter(s -> s.getDayOfWeek() == dayOfWeek)
                .anyMatch(s -> {
                    // Verificar solapamiento
                    return !(endTime.isBefore(s.getStartTime()) || startTime.isAfter(s.getEndTime()));
                });
    }

    /**
     * Obtiene estadísticas de ocupación.
     */
    public Map<DayOfWeek, OccupancyStats> getWeekOccupancyStats() {
        Map<DayOfWeek, OccupancyStats> stats = new HashMap<>();

        for (DayOfWeek day : DayOfWeek.values()) {
            List<Schedule> daySchedules = scheduleRepository.findByDayOfWeek(day);

            int totalMinutes = daySchedules.stream()
                    .mapToInt(s -> (int) java.time.Duration.between(s.getStartTime(), s.getEndTime()).toMinutes())
                    .sum();

            int availableMinutes = (int) java.time.Duration.between(MORNING_START, MORNING_END).toMinutes()
                    + (int) java.time.Duration.between(AFTERNOON_START, AFTERNOON_END).toMinutes();

            double occupancyRate = (double) totalMinutes / availableMinutes * 100;

            stats.put(day, new OccupancyStats(daySchedules.size(), totalMinutes, occupancyRate));
        }

        return stats;
    }

    private String generateScheduleNotes(Course course) {
        StringBuilder notes = new StringBuilder();
        notes.append("Tipo: ").append(course.getCourseType().getDisplayName());
        notes.append(" | Estudiantes: ").append(course.getStudentIds().size());
        if (course.getMaxStudents() != null) {
            notes.append("/").append(course.getMaxStudents());
        }
        return notes.toString();
    }

    // Clases auxiliares
    public record TimeSlot(LocalTime start, LocalTime end) {
        public int getDurationMinutes() {
            return (int) java.time.Duration.between(start, end).toMinutes();
        }
    }

    public record OccupancyStats(int classCount, int totalMinutes, double occupancyRate) {}
}

