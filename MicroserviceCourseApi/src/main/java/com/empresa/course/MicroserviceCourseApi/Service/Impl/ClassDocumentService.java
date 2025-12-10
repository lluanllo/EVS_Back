package com.empresa.course.MicroserviceCourseApi.Service.Impl;

import com.empresa.course.MicroserviceCourseApi.Entities.Course;
import com.empresa.course.MicroserviceCourseApi.Entities.Enums.WindDirection;
import com.empresa.course.MicroserviceCourseApi.Entities.Mongo.ClassDocument;
import com.empresa.course.MicroserviceCourseApi.Entities.RoutePlan;
import com.empresa.course.MicroserviceCourseApi.Entities.WeatherData;
import com.empresa.course.MicroserviceCourseApi.Repository.ClassDocumentRepository;
import com.empresa.course.MicroserviceCourseApi.Repository.CourseRepository;
import com.empresa.course.MicroserviceCourseApi.Service.Inter.IClassDocumentService;
import com.empresa.course.MicroserviceCourseApi.Service.Inter.IWeatherService;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClassDocumentService implements IClassDocumentService {

    private final ClassDocumentRepository classDocumentRepository;
    private final CourseRepository courseRepository;
    private final IWeatherService weatherService;
    private final RoutePlannerService routePlannerService;

    @Override
    public ClassDocument generateClassDocument(Long courseId, Long teacherId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado: " + courseId));

        // Obtener datos meteorológicos actuales
        WeatherData weather = weatherService.getLatestWeather().orElse(null);

        // Obtener clase anterior para resumen
        Optional<ClassDocument> previousDoc = getLatestDocumentByCourse(courseId);

        // Convertir dirección del viento
        WindDirection windDir = weather != null ?
                convertToWindDirection(weather.getWindDirection()) : WindDirection.S;
        int windSpeed = weather != null && weather.getWindSpeed() != null ?
                weather.getWindSpeed().intValue() : 10;

        // Generar plan de ruta con la firma correcta
        RoutePlan routePlan = routePlannerService.generateRoutePlan(
                courseId,
                course.getCourseType(),
                windDir,
                windSpeed,
                course.getDurationMinutes() != null ? course.getDurationMinutes() : 60,
                2 // nivel estudiante por defecto
        );

        // Crear información de la clase
        ClassDocument.ClassInfo classInfo = ClassDocument.ClassInfo.builder()
                .durationMinutes(course.getDurationMinutes())
                .skillLevel(course.getCourseType().name())
                .studentCount(course.getStudentIds().size())
                .weatherConditions(weather != null ?
                        String.format("Viento: %.1f kts %s, Temp: %.1f°C",
                                weather.getWindSpeed(), weather.getWindDirection(), weather.getTemperature())
                        : "Sin datos")
                .windSpeed(weather != null ? weather.getWindSpeed() : null)
                .windDirection(weather != null ? weather.getWindDirection() : null)
                .build();

        // Convertir plan de ruta
        ClassDocument.RoutePlanInfo routePlanInfo = convertRoutePlan(routePlan);

        // Generar PDF
        byte[] pdfContent = generatePdfContent(course, classInfo, routePlanInfo, previousDoc.orElse(null));

        ClassDocument document = ClassDocument.builder()
                .courseId(courseId)
                .teacherId(teacherId)
                .classDate(LocalDateTime.now())
                .courseType(course.getCourseType().name())
                .pdfContent(pdfContent)
                .pdfFilename(String.format("clase_%s_%s.pdf",
                        course.getName(),
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"))))
                .classInfo(classInfo)
                .routePlan(routePlanInfo)
                .previousClassSummary(previousDoc.map(ClassDocument::getClassInfo)
                        .map(info -> ClassDocument.ClassSummary.builder()
                                .date(previousDoc.get().getClassDate())
                                .summary("Clase anterior completada")
                                .build())
                        .orElse(null))
                .createdAt(LocalDateTime.now())
                .sent(false)
                .build();

        ClassDocument saved = classDocumentRepository.save(document);
        log.info("Documento de clase generado: {} para curso {}", saved.getId(), courseId);

        return saved;
    }

    private WindDirection convertToWindDirection(String direction) {
        if (direction == null) return WindDirection.S;
        try {
            String normalized = direction.toUpperCase().trim();
            if (normalized.length() > 2) {
                normalized = normalized.substring(0, 2);
            }
            if (normalized.length() > 1 && !normalized.equals("NE") && !normalized.equals("NW")
                    && !normalized.equals("SE") && !normalized.equals("SW")) {
                normalized = normalized.substring(0, 1);
            }
            return WindDirection.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            return WindDirection.S;
        }
    }

    private ClassDocument.RoutePlanInfo convertRoutePlan(RoutePlan routePlan) {
        if (routePlan == null) return null;

        List<ClassDocument.LegInfo> legs = new ArrayList<>();
        if (routePlan.getLegs() != null) {
            for (int i = 0; i < routePlan.getLegs().size(); i++) {
                var leg = routePlan.getLegs().get(i);
                legs.add(ClassDocument.LegInfo.builder()
                        .legNumber(i + 1)
                        .direction(leg.getHeading() + "°")
                        .maneuver(leg.getManeuverType() != null ? leg.getManeuverType().name() : "NAVEGAR")
                        .durationMinutes(leg.getDurationMinutes())
                        .tackType(leg.getOrder() % 2 == 0 ? "Estribor" : "Babor")
                        .notes("Rumbo " + leg.getHeading() + "°, distancia " + leg.getDistanceMeters() + "m")
                        .build());
            }
        }

        return ClassDocument.RoutePlanInfo.builder()
                .startPoint("Playa La Antilla")
                .endPoint("Playa La Antilla")
                .legs(legs)
                .build();
    }

    private byte[] generatePdfContent(Course course, ClassDocument.ClassInfo classInfo,
                                       ClassDocument.RoutePlanInfo routePlan, ClassDocument previousDoc) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Título
            Paragraph title = new Paragraph("ESCUELA DE VELA - PLAN DE CLASE")
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.DARK_GRAY);
            document.add(title);

            // Información del curso
            document.add(new Paragraph("Curso: " + course.getName())
                    .setFontSize(14));
            document.add(new Paragraph("Tipo: " + course.getCourseType())
                    .setFontSize(12));
            document.add(new Paragraph("Fecha: " + LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                    .setFontSize(12));
            document.add(new Paragraph("Duración: " + classInfo.getDurationMinutes() + " minutos")
                    .setFontSize(12));
            document.add(new Paragraph("Número de alumnos: " + classInfo.getStudentCount())
                    .setFontSize(12));

            // Condiciones meteorológicas
            document.add(new Paragraph("\nCONDICIONES METEOROLÓGICAS")
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    .setFontSize(14));
            document.add(new Paragraph(classInfo.getWeatherConditions())
                    .setFontSize(12));

            // Plan de ruta
            if (routePlan != null && routePlan.getLegs() != null && !routePlan.getLegs().isEmpty()) {
                document.add(new Paragraph("\nPLAN DE NAVEGACIÓN - BORDOS")
                        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                        .setFontSize(14));

                Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2, 2, 2, 3}))
                        .useAllAvailableWidth();

                // Cabecera
                table.addHeaderCell(new Cell().add(new Paragraph("Nº").setBold()));
                table.addHeaderCell(new Cell().add(new Paragraph("Dirección").setBold()));
                table.addHeaderCell(new Cell().add(new Paragraph("Maniobra").setBold()));
                table.addHeaderCell(new Cell().add(new Paragraph("Duración").setBold()));
                table.addHeaderCell(new Cell().add(new Paragraph("Notas").setBold()));

                for (ClassDocument.LegInfo leg : routePlan.getLegs()) {
                    table.addCell(String.valueOf(leg.getLegNumber()));
                    table.addCell(leg.getDirection() != null ? leg.getDirection() : "-");
                    table.addCell(leg.getManeuver() != null ? leg.getManeuver() : "-");
                    table.addCell(leg.getDurationMinutes() != null ? leg.getDurationMinutes() + " min" : "-");
                    table.addCell(leg.getNotes() != null ? leg.getNotes() : "-");
                }

                document.add(table);
            }

            // Maniobras a practicar
            document.add(new Paragraph("\nMANIOBRAS A PRACTICAR")
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    .setFontSize(14));

            List<String> maneuvers = getManeuversForLevel(course.getCourseType().name());
            com.itextpdf.layout.element.List maneuverList = new com.itextpdf.layout.element.List()
                    .setSymbolIndent(12);
            for (String maneuver : maneuvers) {
                maneuverList.add(new ListItem(maneuver));
            }
            document.add(maneuverList);

            // Resumen clase anterior
            if (previousDoc != null) {
                document.add(new Paragraph("\nRESUMEN CLASE ANTERIOR")
                        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                        .setFontSize(14));
                document.add(new Paragraph("Fecha: " + previousDoc.getClassDate()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
                if (previousDoc.getClassInfo() != null) {
                    document.add(new Paragraph("Condiciones: " + previousDoc.getClassInfo().getWeatherConditions()));
                }
            }

            // Notas para el profesor
            document.add(new Paragraph("\nNOTAS PARA EL PROFESOR")
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    .setFontSize(14));
            document.add(new Paragraph("• Revisar material antes de la clase"));
            document.add(new Paragraph("• Comprobar chalecos salvavidas"));
            document.add(new Paragraph("• Briefing de seguridad con alumnos"));
            document.add(new Paragraph("• Mantener comunicación con base"));

            document.close();

            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error generando PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Error generando PDF de clase", e);
        }
    }

    private List<String> getManeuversForLevel(String courseType) {
        return switch (courseType.toUpperCase()) {
            case "INICIACION", "MINICATA" -> List.of(
                    "Posición básica en el barco",
                    "Manejo del timón",
                    "Orzar y arribar",
                    "Virada por avante básica"
            );
            case "CATAMARAN" -> List.of(
                    "Virada por avante",
                    "Trasluchada",
                    "Navegación en ceñida",
                    "Navegación en través",
                    "Navegación en popa"
            );
            case "WINDSURF" -> List.of(
                    "Beach start",
                    "Trasluchada de windsurf",
                    "Navegación en planeo",
                    "Uso del arnés",
                    "Water start (avanzado)"
            );
            default -> List.of(
                    "Técnicas básicas de navegación",
                    "Maniobras fundamentales",
                    "Seguridad en el agua"
            );
        };
    }

    @Override
    public Optional<ClassDocument> getDocumentById(String id) {
        return classDocumentRepository.findById(id);
    }

    @Override
    public Optional<ClassDocument> getLatestDocumentByCourse(Long courseId) {
        return classDocumentRepository.findTopByCourseIdOrderByClassDateDesc(courseId);
    }

    @Override
    public List<ClassDocument> getDocumentsByCourse(Long courseId) {
        return classDocumentRepository.findByCourseIdOrderByClassDateDesc(courseId);
    }

    @Override
    public List<ClassDocument> getDocumentsByTeacher(Long teacherId) {
        return classDocumentRepository.findByTeacherIdOrderByClassDateDesc(teacherId);
    }

    @Override
    public byte[] getPdfContent(String documentId) {
        return classDocumentRepository.findById(documentId)
                .map(ClassDocument::getPdfContent)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado: " + documentId));
    }

    @Override
    public ClassDocument markAsSent(String documentId) {
        ClassDocument doc = classDocumentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado: " + documentId));
        doc.setSent(true);
        doc.setSentAt(LocalDateTime.now());
        return classDocumentRepository.save(doc);
    }

    @Override
    public ClassDocument addClassSummary(String documentId, ClassDocument.ClassSummary summary) {
        ClassDocument doc = classDocumentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado: " + documentId));
        doc.setPreviousClassSummary(summary);
        doc.setUpdatedAt(LocalDateTime.now());
        return classDocumentRepository.save(doc);
    }
}
