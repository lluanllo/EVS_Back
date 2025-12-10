package com.empresa.course.MicroserviceCourseApi.Entities.Mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Documento de clase almacenado en MongoDB
 * Contiene el PDF y toda la información de la clase para el profesor
 */
@Data
@Builder
@Document(collection = "class_documents")
@AllArgsConstructor
@NoArgsConstructor
public class ClassDocument {

    @Id
    private String id;

    private Long courseId;
    private Long teacherId;
    private LocalDateTime classDate;
    private String courseType;

    // PDF binario
    private byte[] pdfContent;
    private String pdfFilename;

    // Información de la clase
    private ClassInfo classInfo;

    // Resumen de la clase anterior
    private ClassSummary previousClassSummary;

    // Ruta planificada
    private RoutePlanInfo routePlan;

    // Metadatos
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean sent;
    private LocalDateTime sentAt;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ClassInfo {
        private Integer durationMinutes;
        private String skillLevel;
        private Integer studentCount;
        private List<StudentInfo> students;
        private String weatherConditions;
        private Double windSpeed;
        private String windDirection;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StudentInfo {
        private Long studentId;
        private String name;
        private String skillLevel;
        private String notes;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ClassSummary {
        private String classDocumentId;
        private LocalDateTime date;
        private String summary;
        private List<String> achievedObjectives;
        private List<String> pendingObjectives;
        private String teacherNotes;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoutePlanInfo {
        private String startPoint;
        private String endPoint;
        private List<LegInfo> legs;
        private byte[] routeImage;
        private String imageFormat;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LegInfo {
        private Integer legNumber;
        private String direction;
        private String maneuver;
        private Integer durationMinutes;
        private String notes;
        private String tackType; // Bordo
    }
}

