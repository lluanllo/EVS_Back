package com.empresa.teacher.MicroserviceTeacherRegisterAPI.Controller;

import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Kafka.Producer.TeacherEventProducer;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final TeacherEventProducer eventProducer;

    /**
     * Envía el horario a un profesor
     */
    @PostMapping("/send-schedule/{teacherId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> sendScheduleNotification(
            @PathVariable Long teacherId,
            @RequestBody String scheduleDetails) {
        notificationService.sendScheduleNotification(teacherId, scheduleDetails);
        return ResponseEntity.ok().build();
    }

    /**
     * Envía recordatorio de confirmación
     */
    @PostMapping("/send-reminder/{teacherId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> sendConfirmationReminder(@PathVariable Long teacherId) {
        notificationService.sendConfirmationRequest(teacherId);
        return ResponseEntity.ok().build();
    }

    /**
     * Envía recordatorios a todos los profesores pendientes
     */
    @PostMapping("/send-pending-reminders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> sendPendingReminders() {
        notificationService.sendPendingConfirmationReminders();
        return ResponseEntity.ok().build();
    }

    /**
     * Profesor confirma o rechaza su horario
     */
    @PostMapping("/confirm/{teacherId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Void> confirmSchedule(
            @PathVariable Long teacherId,
            @RequestParam boolean confirmed,
            @RequestParam(required = false) String reason) {
        // Publicar evento Kafka
        eventProducer.publishScheduleConfirmation(teacherId, confirmed, reason);
        return ResponseEntity.ok().build();
    }

    /**
     * Envía plan de ruta a un profesor
     */
    @PostMapping("/send-route-plan/{teacherId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> sendRoutePlan(
            @PathVariable Long teacherId,
            @RequestParam String routeSummary,
            @RequestParam(required = false) String imageBase64) {
        notificationService.sendRoutePlan(teacherId, routeSummary, imageBase64);
        return ResponseEntity.ok().build();
    }
}

