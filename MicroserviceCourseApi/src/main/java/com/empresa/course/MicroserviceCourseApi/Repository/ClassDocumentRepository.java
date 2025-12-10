package com.empresa.course.MicroserviceCourseApi.Repository;

import com.empresa.course.MicroserviceCourseApi.Entities.Mongo.ClassDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClassDocumentRepository extends MongoRepository<ClassDocument, String> {

    List<ClassDocument> findByCourseIdOrderByClassDateDesc(Long courseId);

    List<ClassDocument> findByTeacherIdOrderByClassDateDesc(Long teacherId);

    Optional<ClassDocument> findTopByCourseIdOrderByClassDateDesc(Long courseId);

    List<ClassDocument> findByClassDateBetween(LocalDateTime start, LocalDateTime end);

    List<ClassDocument> findBySentFalse();

    List<ClassDocument> findByCourseIdAndClassDateBetween(Long courseId, LocalDateTime start, LocalDateTime end);
}

