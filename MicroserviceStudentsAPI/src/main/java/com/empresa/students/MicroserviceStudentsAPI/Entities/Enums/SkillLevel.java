package com.empresa.students.MicroserviceStudentsAPI.Entities.Enums;

/**
 * Nivel de habilidad del estudiante en navegación
 */
public enum SkillLevel {
    BEGINNER("Principiante", 1),
    INTERMEDIATE("Intermedio", 2),
    ADVANCED("Avanzado", 3),
    EXPERT("Experto", 4);

    private final String displayName;
    private final int level;

    SkillLevel(String displayName, int level) {
        this.displayName = displayName;
        this.level = level;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getLevel() {
        return level;
    }
}

