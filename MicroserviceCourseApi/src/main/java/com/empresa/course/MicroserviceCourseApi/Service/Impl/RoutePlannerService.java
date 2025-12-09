package com.empresa.course.MicroserviceCourseApi.Service.Impl;

import com.empresa.course.MicroserviceCourseApi.Entities.Enums.CourseType;
import com.empresa.course.MicroserviceCourseApi.Entities.Enums.ManeuverType;
import com.empresa.course.MicroserviceCourseApi.Entities.Enums.WindDirection;
import com.empresa.course.MicroserviceCourseApi.Entities.NauticalZone;
import com.empresa.course.MicroserviceCourseApi.Entities.RouteLeg;
import com.empresa.course.MicroserviceCourseApi.Entities.RoutePlan;
import com.empresa.course.MicroserviceCourseApi.Repository.NauticalZoneRepository;
import com.empresa.course.MicroserviceCourseApi.Repository.RoutePlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de planificación de rutas de navegación.
 * Genera planes de ruta basados en:
 * - Dirección y velocidad del viento
 * - Tipo de embarcación/clase
 * - Nivel del estudiante
 * - Duración de la clase
 * - Configuración de la playa
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoutePlannerService {

    private final RoutePlanRepository routePlanRepository;
    private final NauticalZoneRepository nauticalZoneRepository;
    private final RouteImageGeneratorService imageGenerator;

    @Value("${sailing.beach.latitude:37.2074}")
    private Double beachLatitude;

    @Value("${sailing.beach.longitude:-7.2193}")
    private Double beachLongitude;

    @Value("${sailing.beach.orientation:180}")
    private Integer beachOrientation;

    // Constantes de navegación
    private static final int ANGLE_CENIDA = 45;  // Ángulo mínimo respecto al viento para ceñir
    private static final int ANGLE_TRAVES = 90;  // Navegación a través
    private static final int ANGLE_LARGO = 135;  // Navegación en largo
    private static final double KNOTS_TO_MS = 0.514444; // Conversión nudos a m/s

    /**
     * Genera un plan de ruta completo para una clase.
     */
    @Transactional
    public RoutePlan generateRoutePlan(
            Long courseId,
            CourseType courseType,
            WindDirection windDirection,
            int windSpeedKnots,
            int classDurationMinutes,
            int studentLevel) {

        log.info("Generando plan de ruta para {} - Viento: {} a {} nudos - Nivel: {}",
                courseType, windDirection, windSpeedKnots, studentLevel);

        // Crear plan base
        RoutePlan plan = RoutePlan.builder()
                .courseId(courseId)
                .courseType(courseType)
                .windDirection(windDirection)
                .windSpeedKnots(windSpeedKnots)
                .classDurationMinutes(classDurationMinutes)
                .studentLevel(studentLevel)
                .beachLatitude(beachLatitude)
                .beachLongitude(beachLongitude)
                .beachOrientation(beachOrientation)
                .legs(new ArrayList<>())
                .build();

        // Obtener zonas navegables según nivel
        List<NauticalZone> allowedZones = nauticalZoneRepository.findByMinLevelLessThanEqual(studentLevel);

        // Calcular parámetros de navegación según nivel
        NavigationParams params = calculateNavigationParams(studentLevel, windSpeedKnots, courseType);

        // Generar los tramos de la ruta
        List<RouteLeg> legs = generateLegs(plan, params, windDirection, classDurationMinutes);
        plan.setLegs(legs);

        // Generar resumen
        plan.setSummary(generateSummary(plan, legs));

        // Generar notas de seguridad
        plan.setSafetyNotes(generateSafetyNotes(windSpeedKnots, studentLevel, courseType));

        // Guardar plan
        plan = routePlanRepository.save(plan);

        // Generar imagen de la ruta
        try {
            String imageBase64 = imageGenerator.generateRouteImage(plan);
            plan.setImageBase64(imageBase64);
            plan = routePlanRepository.save(plan);
        } catch (Exception e) {
            log.error("Error generando imagen de ruta: {}", e.getMessage());
        }

        return plan;
    }

    /**
     * Calcula los parámetros de navegación según el nivel del estudiante.
     */
    private NavigationParams calculateNavigationParams(int studentLevel, int windSpeedKnots, CourseType courseType) {
        NavigationParams params = new NavigationParams();

        // Distancia máxima desde la playa (metros)
        switch (studentLevel) {
            case 1 -> params.maxDistance = 200;  // Principiante
            case 2 -> params.maxDistance = 400;  // Intermedio
            case 3 -> params.maxDistance = 600;  // Avanzado
            default -> params.maxDistance = 800; // Experto
        }

        // Velocidad estimada según tipo de embarcación y viento (nudos)
        double baseSpeed = switch (courseType) {
            case WINDSURF -> windSpeedKnots * 0.4;
            case CATAMARAN -> windSpeedKnots * 0.6;
            case MINICATA -> windSpeedKnots * 0.5;
            case OPTIMIST -> windSpeedKnots * 0.3;
            case PADDLE_SURF -> 2.0; // Velocidad fija
            case KAYAK -> 3.0;
            default -> windSpeedKnots * 0.4;
        };
        params.estimatedSpeed = Math.max(2, Math.min(baseSpeed, 12)); // Entre 2 y 12 nudos

        // Tiempo máximo por bordo según nivel (minutos)
        params.maxLegDuration = switch (studentLevel) {
            case 1 -> 5;
            case 2 -> 8;
            case 3 -> 12;
            default -> 15;
        };

        // Maniobras permitidas según nivel
        params.canTack = studentLevel >= 1;
        params.canGybe = studentLevel >= 2;
        params.canSailUpwind = studentLevel >= 2;

        return params;
    }

    /**
     * Genera los tramos de la ruta.
     */
    private List<RouteLeg> generateLegs(RoutePlan plan, NavigationParams params,
                                         WindDirection windDirection, int totalMinutes) {
        List<RouteLeg> legs = new ArrayList<>();
        int remainingMinutes = totalMinutes;
        int order = 1;

        double currentLat = beachLatitude;
        double currentLng = beachLongitude;
        int windDegrees = windDirection.getDegrees();

        // 1. Salida desde la playa
        RouteLeg departure = createLeg(order++, ManeuverType.SALIDA, 3,
                calculateHeadingForDeparture(windDegrees, beachOrientation),
                100, currentLat, currentLng, params.estimatedSpeed);
        legs.add(departure);
        remainingMinutes -= departure.getDurationMinutes();
        currentLat = departure.getEndLatitude();
        currentLng = departure.getEndLongitude();

        // 2. Navegación principal - alternar rumbos
        boolean goingLeft = true;
        while (remainingMinutes > 10) { // Guardar tiempo para volver
            int legDuration = Math.min(params.maxLegDuration, remainingMinutes / 3);

            // Calcular rumbo de navegación
            int heading = calculateSailingHeading(windDegrees, goingLeft, params.canSailUpwind);
            ManeuverType maneuver = determineManeuverType(heading, windDegrees);

            // Calcular distancia
            double speedMs = params.estimatedSpeed * KNOTS_TO_MS;
            int distanceMeters = (int) (speedMs * legDuration * 60);

            // Verificar que no nos alejamos demasiado
            if (calculateDistanceFromBeach(currentLat, currentLng) + distanceMeters > params.maxDistance) {
                // Cambiar de bordo
                goingLeft = !goingLeft;

                // Añadir virada o trasluchada
                ManeuverType turnType = goingLeft ? ManeuverType.VIRADA : ManeuverType.TRASLUCHADA;
                if (params.canTack || turnType == ManeuverType.VIRADA) {
                    RouteLeg turn = createLeg(order++, turnType, 1,
                            heading, 0, currentLat, currentLng, 0);
                    legs.add(turn);
                    remainingMinutes -= 1;
                }
                continue;
            }

            // Crear tramo de navegación
            RouteLeg leg = createLeg(order++, maneuver, legDuration,
                    heading, distanceMeters, currentLat, currentLng, params.estimatedSpeed);
            legs.add(leg);
            remainingMinutes -= legDuration;
            currentLat = leg.getEndLatitude();
            currentLng = leg.getEndLongitude();
            goingLeft = !goingLeft;

            // Añadir virada
            if (remainingMinutes > 10 && params.canTack) {
                RouteLeg turn = createLeg(order++, ManeuverType.VIRADA, 1,
                        heading, 0, currentLat, currentLng, 0);
                legs.add(turn);
                remainingMinutes -= 1;
            }
        }

        // 3. Regreso a la playa
        int returnHeading = calculateReturnHeading(currentLat, currentLng, beachLatitude, beachLongitude);
        int returnDistance = (int) calculateDistanceFromBeach(currentLat, currentLng);
        RouteLeg returnLeg = createLeg(order++, ManeuverType.LLEGADA, remainingMinutes - 2,
                returnHeading, returnDistance, currentLat, currentLng, params.estimatedSpeed);
        legs.add(returnLeg);

        return legs;
    }

    private RouteLeg createLeg(int order, ManeuverType maneuver, int durationMinutes,
                                int heading, int distanceMeters, double startLat, double startLng,
                                double speedKnots) {
        // Calcular punto final
        double[] endPoint = calculateEndPoint(startLat, startLng, heading, distanceMeters);

        return RouteLeg.builder()
                .order(order)
                .maneuverType(maneuver)
                .durationMinutes(durationMinutes)
                .heading(heading)
                .distanceMeters(distanceMeters)
                .startLatitude(startLat)
                .startLongitude(startLng)
                .endLatitude(endPoint[0])
                .endLongitude(endPoint[1])
                .description(generateLegDescription(maneuver, heading, durationMinutes))
                .build();
    }

    private int calculateHeadingForDeparture(int windDegrees, int beachOrientation) {
        // Salir perpendicular a la playa, hacia donde viene el viento
        int optimalHeading = (beachOrientation + 180) % 360;
        // Ajustar para no ir directamente contra el viento
        if (Math.abs(optimalHeading - windDegrees) < ANGLE_CENIDA) {
            optimalHeading = (optimalHeading + ANGLE_CENIDA) % 360;
        }
        return optimalHeading;
    }

    private int calculateSailingHeading(int windDegrees, boolean goLeft, boolean canSailUpwind) {
        int offset = canSailUpwind ? ANGLE_CENIDA : ANGLE_TRAVES;
        if (goLeft) {
            return (windDegrees + offset) % 360;
        } else {
            return (windDegrees - offset + 360) % 360;
        }
    }

    private ManeuverType determineManeuverType(int heading, int windDegrees) {
        int angleDiff = Math.abs(heading - windDegrees);
        if (angleDiff > 180) angleDiff = 360 - angleDiff;

        if (angleDiff <= 60) return ManeuverType.CENIDA;
        if (angleDiff <= 120) return ManeuverType.TRAVES;
        if (angleDiff <= 150) return ManeuverType.LARGO;
        return ManeuverType.POPA;
    }

    private double calculateDistanceFromBeach(double lat, double lng) {
        // Fórmula simplificada de Haversine
        double dLat = Math.toRadians(lat - beachLatitude);
        double dLng = Math.toRadians(lng - beachLongitude);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(Math.toRadians(beachLatitude)) * Math.cos(Math.toRadians(lat)) *
                   Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return 6371000 * c; // Radio de la tierra en metros
    }

    private int calculateReturnHeading(double fromLat, double fromLng, double toLat, double toLng) {
        double dLng = Math.toRadians(toLng - fromLng);
        double y = Math.sin(dLng) * Math.cos(Math.toRadians(toLat));
        double x = Math.cos(Math.toRadians(fromLat)) * Math.sin(Math.toRadians(toLat)) -
                   Math.sin(Math.toRadians(fromLat)) * Math.cos(Math.toRadians(toLat)) * Math.cos(dLng);
        double bearing = Math.atan2(y, x);
        return (int) ((Math.toDegrees(bearing) + 360) % 360);
    }

    private double[] calculateEndPoint(double lat, double lng, int heading, int distanceMeters) {
        double R = 6371000; // Radio de la tierra en metros
        double d = distanceMeters / R;
        double brng = Math.toRadians(heading);
        double lat1 = Math.toRadians(lat);
        double lng1 = Math.toRadians(lng);

        double lat2 = Math.asin(Math.sin(lat1) * Math.cos(d) +
                               Math.cos(lat1) * Math.sin(d) * Math.cos(brng));
        double lng2 = lng1 + Math.atan2(Math.sin(brng) * Math.sin(d) * Math.cos(lat1),
                                        Math.cos(d) - Math.sin(lat1) * Math.sin(lat2));

        return new double[]{Math.toDegrees(lat2), Math.toDegrees(lng2)};
    }

    private String generateLegDescription(ManeuverType maneuver, int heading, int durationMinutes) {
        String direction = getCompassDirection(heading);
        return String.format("%s - Rumbo %d° (%s) - %d minutos",
                maneuver.getDisplayName(), heading, direction, durationMinutes);
    }

    private String getCompassDirection(int heading) {
        if (heading >= 337.5 || heading < 22.5) return "N";
        if (heading >= 22.5 && heading < 67.5) return "NE";
        if (heading >= 67.5 && heading < 112.5) return "E";
        if (heading >= 112.5 && heading < 157.5) return "SE";
        if (heading >= 157.5 && heading < 202.5) return "S";
        if (heading >= 202.5 && heading < 247.5) return "SW";
        if (heading >= 247.5 && heading < 292.5) return "W";
        return "NW";
    }

    private String generateSummary(RoutePlan plan, List<RouteLeg> legs) {
        int totalDistance = legs.stream().mapToInt(RouteLeg::getDistanceMeters).sum();
        long viradas = legs.stream().filter(l -> l.getManeuverType() == ManeuverType.VIRADA).count();
        long trasluchadas = legs.stream().filter(l -> l.getManeuverType() == ManeuverType.TRASLUCHADA).count();

        return String.format(
                "Plan de navegación para %s\n" +
                "Viento: %s a %d nudos\n" +
                "Duración: %d minutos\n" +
                "Distancia total: %d metros\n" +
                "Viradas: %d | Trasluchadas: %d\n" +
                "Total tramos: %d",
                plan.getCourseType().getDisplayName(),
                plan.getWindDirection().getDisplayName(),
                plan.getWindSpeedKnots(),
                plan.getClassDurationMinutes(),
                totalDistance,
                viradas, trasluchadas,
                legs.size()
        );
    }

    private String generateSafetyNotes(int windSpeedKnots, int studentLevel, CourseType courseType) {
        StringBuilder notes = new StringBuilder();

        if (windSpeedKnots > 15) {
            notes.append("⚠️ Viento fuerte - Extremar precauciones\n");
        }
        if (windSpeedKnots > 20 && studentLevel < 3) {
            notes.append("⚠️ Condiciones no recomendadas para este nivel\n");
        }
        if (studentLevel == 1) {
            notes.append("📍 Mantener siempre contacto visual con la playa\n");
            notes.append("📍 No alejarse más de 200m de la orilla\n");
        }

        notes.append("📍 Llevar siempre chaleco salvavidas\n");
        notes.append("📍 Comunicar cualquier problema al instructor\n");

        return notes.toString();
    }

    /**
     * Clase auxiliar para parámetros de navegación
     */
    private static class NavigationParams {
        int maxDistance;
        double estimatedSpeed;
        int maxLegDuration;
        boolean canTack;
        boolean canGybe;
        boolean canSailUpwind;
    }
}

