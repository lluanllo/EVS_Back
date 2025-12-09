package com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Enums;

/**
 * Tipo de contrato del profesor con orden de prioridad
 * Los fijos tienen mayor prioridad, seguidos de temporales y prácticas
 */
public enum ContractType {
    FIJO(1, "Fijo"),
    TEMPORAL(2, "Temporal"),
    PRACTICAS(3, "Prácticas");

    private final int priority;
    private final String displayName;

    ContractType(int priority, String displayName) {
        this.priority = priority;
        this.displayName = displayName;
    }

    public int getPriority() {
        return priority;
    }

    public String getDisplayName() {
        return displayName;
    }
}

