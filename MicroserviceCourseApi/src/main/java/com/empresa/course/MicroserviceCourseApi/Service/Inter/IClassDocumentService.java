package com.empresa.course.MicroserviceCourseApi.Service.Inter;

import com.empresa.course.MicroserviceCourseApi.Entities.Mongo.ClassDocument;

import java.util.List;
import java.util.Optional;

public interface IClassDocumentService {

    /**
     * Genera un documento PDF para una clase
     */
    ClassDocument generateClassDocument(Long courseId, Long teacherId);

    /**
     * Obtiene un documento por ID
     */
    Optional<ClassDocument> getDocumentById(String id);

    /**
     * Obtiene el último documento de un curso
     */
    Optional<ClassDocument> getLatestDocumentByCourse(Long courseId);

    /**
     * Obtiene todos los documentos de un curso
     */
    List<ClassDocument> getDocumentsByCourse(Long courseId);

    /**
     * Obtiene todos los documentos de un profesor
     */
    List<ClassDocument> getDocumentsByTeacher(Long teacherId);

    /**
     * Obtiene el PDF como bytes
     */
    byte[] getPdfContent(String documentId);

    /**
     * Marca un documento como enviado
     */
    ClassDocument markAsSent(String documentId);

    /**
     * Añade un resumen de clase
     */
    ClassDocument addClassSummary(String documentId, ClassDocument.ClassSummary summary);
}

