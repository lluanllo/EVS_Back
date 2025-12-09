package com.empresa.teacher.MicroserviceTeacherRegisterAPI.Service;

import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Teacher;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de notificaciones para profesores
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;
    private final TeacherRepository teacherRepository;

    @Value("${spring.mail.username:no-reply@evs-laantilla.com}")
    private String fromEmail;

    /**
     * Envía el horario semanal a un profesor
     */
    public void sendScheduleNotification(Long teacherId, String scheduleDetails) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado: " + teacherId));

        String subject = "📅 Tu horario semanal - EVS La Antilla";
        String body = String.format("""
                Hola %s,
                
                Aquí tienes tu horario para la próxima semana:
                
                %s
                
                Por favor, confirma tu disponibilidad respondiendo a este email o 
                accediendo a la aplicación.
                
                ⚠️ IMPORTANTE: Tienes 24 horas para confirmar tu disponibilidad.
                Si no confirmas, se reasignarán tus clases a otro profesor.
                
                ¡Gracias!
                
                --
                Escuela de Vela La Antilla
                """, teacher.getName(), scheduleDetails);

        sendEmail(teacher.getEmail(), subject, body);
        log.info("Horario enviado a {} ({})", teacher.getName(), teacher.getEmail());
    }

    /**
     * Envía notificación de confirmación requerida
     */
    public void sendConfirmationRequest(Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado: " + teacherId));

        String subject = "⏰ Confirmación pendiente - EVS La Antilla";
        String body = String.format("""
                Hola %s,
                
                Recordatorio: Tienes un horario pendiente de confirmar.
                
                Por favor, accede a la aplicación para confirmar tu disponibilidad
                antes de que se reasignen tus clases.
                
                --
                Escuela de Vela La Antilla
                """, teacher.getName());

        sendEmail(teacher.getEmail(), subject, body);
    }

    /**
     * Envía notificación de reasignación
     */
    public void sendReassignmentNotification(Long teacherId, String reason) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado: " + teacherId));

        String subject = "🔄 Reasignación de clases - EVS La Antilla";
        String body = String.format("""
                Hola %s,
                
                Te informamos que tus clases han sido reasignadas a otro profesor.
                
                Motivo: %s
                
                Si tienes alguna pregunta, contacta con administración.
                
                --
                Escuela de Vela La Antilla
                """, teacher.getName(), reason);

        sendEmail(teacher.getEmail(), subject, body);
    }

    /**
     * Envía plan de ruta a un profesor
     */
    public void sendRoutePlan(Long teacherId, String routeSummary, String imageBase64) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado: " + teacherId));

        String subject = "🚤 Plan de navegación para tu clase - EVS La Antilla";
        String body = String.format("""
                Hola %s,
                
                Aquí tienes el plan de navegación para tu próxima clase:
                
                %s
                
                La imagen del recorrido está adjunta a este email.
                
                ¡Buena navegación!
                
                --
                Escuela de Vela La Antilla
                """, teacher.getName(), routeSummary);

        // En una implementación real, aquí se adjuntaría la imagen
        sendEmail(teacher.getEmail(), subject, body);
        log.info("Plan de ruta enviado a {} ({})", teacher.getName(), teacher.getEmail());
    }

    /**
     * Envía recordatorio a profesores que no han confirmado
     */
    public void sendPendingConfirmationReminders() {
        List<Teacher> pendingTeachers = teacherRepository.findByConfirmedAvailabilityFalse();

        for (Teacher teacher : pendingTeachers) {
            try {
                sendConfirmationRequest(teacher.getId());
            } catch (Exception e) {
                log.error("Error enviando recordatorio a {}: {}", teacher.getEmail(), e.getMessage());
            }
        }

        log.info("Enviados {} recordatorios de confirmación", pendingTeachers.size());
    }

    private void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.debug("Email enviado a {}", to);
        } catch (Exception e) {
            log.error("Error enviando email a {}: {}", to, e.getMessage());
            // No lanzar excepción para no interrumpir el flujo
        }
    }
}

