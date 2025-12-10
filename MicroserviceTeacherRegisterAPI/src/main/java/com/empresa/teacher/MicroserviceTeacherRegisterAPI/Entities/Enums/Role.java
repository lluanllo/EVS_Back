package com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Enums;

/**
 * Roles del sistema para autenticación y autorización
 */
public enum Role {
    ADMIN,      // Administrador del sistema
    BOSS,       // Dueños de la escuela - control total, eventos, regatas, pagos
    TEACHER,    // Profesores
    STUDENT     // Alumnos
}

