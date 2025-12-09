package com.empresa.course.MicroserviceCourseApi.Entities.Enums;

/**
 * Tipos de cursos/actividades disponibles en la escuela de vela
 */
public enum CourseType {
    WINDSURF("Windsurf", 2, 6),
    CATAMARAN("Catamarán", 2, 4),
    MINICATA("Minicatamarán", 2, 6),
    OPTIMIST("Optimist", 1, 4),
    PADDLE_SURF("Paddle Surf", 1, 8),
    KAYAK("Kayak", 1, 6),
    SUMMER_CAMP("Campamento de Verano", 3, 15),
    VELA_LIGERA("Vela Ligera", 2, 4);

    private final String displayName;
    private final int minInstructors;
    private final int maxStudentsPerInstructor;

    CourseType(String displayName, int minInstructors, int maxStudentsPerInstructor) {
        this.displayName = displayName;
        this.minInstructors = minInstructors;
        this.maxStudentsPerInstructor = maxStudentsPerInstructor;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMinInstructors() {
        return minInstructors;
    }

    public int getMaxStudentsPerInstructor() {
        return maxStudentsPerInstructor;
    }
}

