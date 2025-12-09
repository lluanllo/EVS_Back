package com.empresa.course.MicroserviceCourseApi.Entities.Enums;

/**
 * Dirección del viento para planificación de clases
 */
public enum WindDirection {
    N(0, "Norte"),
    NE(45, "Noreste"),
    E(90, "Este"),
    SE(135, "Sureste"),
    S(180, "Sur"),
    SW(225, "Suroeste"),
    W(270, "Oeste"),
    NW(315, "Noroeste");

    private final int degrees;
    private final String displayName;

    WindDirection(int degrees, String displayName) {
        this.degrees = degrees;
        this.displayName = displayName;
    }

    public int getDegrees() {
        return degrees;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Obtiene la dirección del viento a partir de grados
     */
    public static WindDirection fromDegrees(int degrees) {
        degrees = ((degrees % 360) + 360) % 360; // Normalizar a 0-359
        if (degrees >= 337.5 || degrees < 22.5) return N;
        if (degrees >= 22.5 && degrees < 67.5) return NE;
        if (degrees >= 67.5 && degrees < 112.5) return E;
        if (degrees >= 112.5 && degrees < 157.5) return SE;
        if (degrees >= 157.5 && degrees < 202.5) return S;
        if (degrees >= 202.5 && degrees < 247.5) return SW;
        if (degrees >= 247.5 && degrees < 292.5) return W;
        return NW;
    }
}

