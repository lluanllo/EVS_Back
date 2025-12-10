package com.empresa.teacher.MicroserviceTeacherRegisterAPI.Service.Inter;

/**
 * Interfaz para el envío de notificaciones (Single Responsibility)
 */
public interface INotificationService {

    void sendScheduleNotification(Long teacherId, Long scheduleId);

    void sendScheduleConfirmationRequest(Long teacherId, Long scheduleId);

    void sendScheduleRejectionNotification(Long teacherId, Long scheduleId, String reason);

    void sendPayrollNotification(Long teacherId, Long payrollId);

    void sendWelcomeEmail(Long teacherId);

    void sendEmailNotification(String email, String subject, String body);
}

