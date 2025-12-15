package com.empresa.course.MicroserviceCourseApi.Entities.Enums;

/**
 * Tipo de maniobra en navegación
 */
public enum ManeuverType {
    VIRADA("Virada", "Cambio de bordo pasando la proa por el viento"),
    TRASLUCHADA("Trasluchada", "Cambio de bordo pasando la popa por el viento"),
    CENIDA("Ceñida", "Navegación contra el viento"),
    TRAVES("Través", "Navegación perpendicular al viento"),
    LARGO("Largo", "Navegación con viento de aleta"),
    POPA("Popa", "Navegación con viento de popa"),
    SALIDA("Salida", "Salida desde la playa"),
    LLEGADA("Llegada", "Llegada a la playa");

    private final String displayName;
    private final String description;

    ManeuverType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
