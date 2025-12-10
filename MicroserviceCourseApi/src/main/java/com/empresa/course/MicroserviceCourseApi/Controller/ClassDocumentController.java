package com.empresa.course.MicroserviceCourseApi.Controller;

import com.empresa.course.MicroserviceCourseApi.Entities.Mongo.ClassDocument;
import com.empresa.course.MicroserviceCourseApi.Service.Inter.IClassDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/class-documents")
@RequiredArgsConstructor
public class ClassDocumentController {

    private final IClassDocumentService classDocumentService;

    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'TEACHER')")
    public ResponseEntity<ClassDocument> generateClassDocument(
            @RequestParam Long courseId,
            @RequestParam Long teacherId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(classDocumentService.generateClassDocument(courseId, teacherId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'TEACHER')")
    public ResponseEntity<ClassDocument> getDocumentById(@PathVariable String id) {
        return classDocumentService.getDocumentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'TEACHER')")
    public ResponseEntity<List<ClassDocument>> getDocumentsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(classDocumentService.getDocumentsByCourse(courseId));
    }

    @GetMapping("/course/{courseId}/latest")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'TEACHER')")
    public ResponseEntity<ClassDocument> getLatestDocumentByCourse(@PathVariable Long courseId) {
        return classDocumentService.getLatestDocumentByCourse(courseId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'TEACHER')")
    public ResponseEntity<List<ClassDocument>> getDocumentsByTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(classDocumentService.getDocumentsByTeacher(teacherId));
    }

    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'TEACHER')")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable String id) {
        try {
            byte[] pdfContent = classDocumentService.getPdfContent(id);
            ClassDocument doc = classDocumentService.getDocumentById(id).orElse(null);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename(doc != null ? doc.getPdfFilename() : "clase.pdf")
                    .build());
            headers.setContentLength(pdfContent.length);

            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/mark-sent")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'TEACHER')")
    public ResponseEntity<ClassDocument> markAsSent(@PathVariable String id) {
        return ResponseEntity.ok(classDocumentService.markAsSent(id));
    }

    @PostMapping("/{id}/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'TEACHER')")
    public ResponseEntity<ClassDocument> addClassSummary(
            @PathVariable String id,
            @RequestBody ClassDocument.ClassSummary summary) {
        return ResponseEntity.ok(classDocumentService.addClassSummary(id, summary));
    }
}

