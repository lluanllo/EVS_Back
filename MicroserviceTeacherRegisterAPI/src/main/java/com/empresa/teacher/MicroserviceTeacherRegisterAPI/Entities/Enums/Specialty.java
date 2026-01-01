package com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Enums;

/**
 * Alias para Speciality (mantiene compatibilidad con código que usa Specialty)
 */
public enum Specialty {
    WINDSURF("Windsurf"),
    CATAMARAN("Catamarán"),
    MINICATA("Minicatamarán"),
    OPTIMIST("Optimist"),
    PADDLE_SURF("Paddle Surf"),
    KAYAK("Kayak"),
    SUMMER_CAMP("Campamento de Verano"),
    VELA_LIGERA("Vela Ligera");

    private final String displayName;

    Specialty(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

