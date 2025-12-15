package com.empresa.teacher.MicroserviceTeacherRegisterAPI.Service;

import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Enums.ContractType;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Enums.Specialty;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Teacher;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio de asignación de profesores a clases.
 * Implementa un algoritmo que:
 * 1. Prioriza por tipo de contrato (FIJO > TEMPORAL > PRACTICAS)
 * 2. Distribuye equitativamente las horas de trabajo
 * 3. Respeta las especialidades de cada profesor
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TeacherAssignmentService {

    private final TeacherRepository teacherRepository;

    /**
     * Asigna el mejor profesor disponible para una clase.
     *
     * @param speciality Especialidad requerida para la clase
     * @param durationMinutes Duración de la clase en minutos
     * @return Optional con el profesor asignado o vacío si no hay disponible
     */
    @Transactional
    public Optional<Teacher> assignTeacher(Specialty speciality, int durationMinutes) {
        log.info("Buscando profesor para {} con duración {} minutos", speciality, durationMinutes);

        // Obtener profesores disponibles con la especialidad, ordenados por prioridad y horas
        List<Teacher> candidates = teacherRepository.findAvailableBySpecialityOrderByPriorityAndHours(speciality);

        if (candidates.isEmpty()) {
            log.warn("No hay profesores disponibles para {}", speciality);
            return Optional.empty();
        }

        // Filtrar por horas máximas semanales
        int hoursToAdd = durationMinutes / 60;
        candidates = candidates.stream()
                .filter(t -> t.getWorkedHours() + hoursToAdd <= t.getMaxWeeklyHours())
                .collect(Collectors.toList());

        if (candidates.isEmpty()) {
            log.warn("Todos los profesores de {} han alcanzado su límite de horas", speciality);
            return Optional.empty();
        }

        // Aplicar algoritmo de selección
        Teacher selected = selectTeacher(candidates, hoursToAdd);

        if (selected != null) {
            // Actualizar horas trabajadas
            selected.setWorkedHours(selected.getWorkedHours() + hoursToAdd);
            teacherRepository.save(selected);
            log.info("Profesor asignado: {} {} - Horas acumuladas: {}",
                selected.getName(), selected.getLastName(), selected.getWorkedHours());
        }

        return Optional.ofNullable(selected);
    }

    /**
     * Selecciona el profesor más adecuado según el algoritmo de prioridad y equidad.
     */
    private Teacher selectTeacher(List<Teacher> candidates, int hoursToAdd) {
        if (candidates.isEmpty()) {
            return null;
        }

        // Agrupar por tipo de contrato
        Map<ContractType, List<Teacher>> byContract = candidates.stream()
                .collect(Collectors.groupingBy(Teacher::getContractType));

        // Procesar en orden de prioridad
        for (ContractType contractType : ContractType.values()) {
            List<Teacher> group = byContract.get(contractType);
            if (group != null && !group.isEmpty()) {
                // Dentro del mismo tipo de contrato, seleccionar el que tiene menos horas
                // para mantener equidad
                return selectMostEquitable(group);
            }
        }

        return null;
    }

    /**
     * Selecciona el profesor con menos horas trabajadas (equidad).
     * En caso de empate, alterna para distribuir mejor.
     */
    private Teacher selectMostEquitable(List<Teacher> teachers) {
        if (teachers.size() == 1) {
            return teachers.get(0);
        }

        // Ordenar por horas trabajadas
        teachers.sort(Comparator.comparingInt(Teacher::getWorkedHours));

        // Si hay varios con las mismas horas mínimas, seleccionar aleatoriamente
        int minHours = teachers.get(0).getWorkedHours();
        List<Teacher> tied = teachers.stream()
                .filter(t -> t.getWorkedHours() == minHours)
                .collect(Collectors.toList());

        if (tied.size() > 1) {
            // Aleatoriedad para distribución equitativa
            Collections.shuffle(tied);
        }

        return tied.get(0);
    }

    /**
     * Asigna múltiples profesores para una clase que requiere varios instructores.
     */
    @Transactional
    public List<Teacher> assignMultipleTeachers(Specialty speciality, int durationMinutes, int count) {
        List<Teacher> assigned = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Optional<Teacher> teacher = assignTeacherExcluding(speciality, durationMinutes, assigned);
            teacher.ifPresent(assigned::add);
        }

        if (assigned.size() < count) {
            log.warn("Solo se pudieron asignar {} de {} profesores para {}",
                assigned.size(), count, speciality);
        }

        return assigned;
    }

    /**
     * Asigna un profesor excluyendo los ya asignados.
     */
    private Optional<Teacher> assignTeacherExcluding(Specialty speciality, int durationMinutes,
                                                      List<Teacher> excluded) {
        Set<Long> excludedIds = excluded.stream()
                .map(Teacher::getId)
                .collect(Collectors.toSet());

        List<Teacher> candidates = teacherRepository.findAvailableBySpecialityOrderByPriorityAndHours(speciality)
                .stream()
                .filter(t -> !excludedIds.contains(t.getId()))
                .filter(t -> t.getWorkedHours() + (durationMinutes / 60) <= t.getMaxWeeklyHours())
                .collect(Collectors.toList());

        if (candidates.isEmpty()) {
            return Optional.empty();
        }

        Teacher selected = selectTeacher(candidates, durationMinutes / 60);
        if (selected != null) {
            selected.setWorkedHours(selected.getWorkedHours() + (durationMinutes / 60));
            teacherRepository.save(selected);
        }

        return Optional.ofNullable(selected);
    }

    /**
     * Reasigna un profesor cuando el actual no está disponible.
     * Mantiene las reglas de prioridad pero excluye al profesor original.
     */
    @Transactional
    public Optional<Teacher> reassignTeacher(Long originalTeacherId, Specialty speciality, int durationMinutes) {
        log.info("Reasignando profesor para {}, profesor original: {}", speciality, originalTeacherId);

        List<Teacher> candidates = teacherRepository.findAvailableBySpecialityOrderByPriorityAndHours(speciality)
                .stream()
                .filter(t -> !t.getId().equals(originalTeacherId))
                .filter(t -> t.getWorkedHours() + (durationMinutes / 60) <= t.getMaxWeeklyHours())
                .collect(Collectors.toList());

        if (candidates.isEmpty()) {
            log.warn("No hay profesores disponibles para reasignación de {}", speciality);
            return Optional.empty();
        }

        Teacher selected = selectTeacher(candidates, durationMinutes / 60);
        if (selected != null) {
            selected.setWorkedHours(selected.getWorkedHours() + (durationMinutes / 60));
            teacherRepository.save(selected);
            log.info("Reasignado a: {} {}", selected.getName(), selected.getLastName());
        }

        return Optional.ofNullable(selected);
    }

    /**
     * Obtiene estadísticas de distribución de horas por tipo de contrato.
     */
    public Map<ContractType, Double> getHoursDistributionStats() {
        Map<ContractType, Double> stats = new HashMap<>();

        for (ContractType type : ContractType.values()) {
            List<Teacher> teachers = teacherRepository.findByContractTypeOrderByWorkedHoursAsc(type);
            if (!teachers.isEmpty()) {
                double avgHours = teachers.stream()
                        .mapToInt(Teacher::getWorkedHours)
                        .average()
                        .orElse(0.0);
                stats.put(type, avgHours);
            }
        }

        return stats;
    }

    /**
     * Verifica si la distribución de horas es equitativa dentro de cada grupo.
     * Retorna true si la diferencia entre el máximo y mínimo es menor al umbral.
     */
    public boolean isDistributionEquitable(ContractType contractType, int thresholdHours) {
        List<Teacher> teachers = teacherRepository.findByContractTypeOrderByWorkedHoursAsc(contractType);

        if (teachers.size() < 2) {
            return true;
        }

        int minHours = teachers.get(0).getWorkedHours();
        int maxHours = teachers.get(teachers.size() - 1).getWorkedHours();

        return (maxHours - minHours) <= thresholdHours;
    }
}
