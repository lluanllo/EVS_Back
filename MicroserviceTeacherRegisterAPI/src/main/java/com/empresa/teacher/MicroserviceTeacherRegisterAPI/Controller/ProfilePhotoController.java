package com.empresa.teacher.MicroserviceTeacherRegisterAPI.Controller;

import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Mongo.ProfilePhoto;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Service.ProfilePhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class ProfilePhotoController {

    private final ProfilePhotoService profilePhotoService;

    @PostMapping("/{teacherId}/photo")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'TEACHER')")
    public ResponseEntity<Map<String, Object>> uploadPhoto(
            @PathVariable Long teacherId,
            @RequestParam("file") MultipartFile file) {
        try {
            ProfilePhoto photo = profilePhotoService.uploadPhoto(teacherId, file);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Foto subida correctamente",
                    "photoId", photo.getId(),
                    "filename", photo.getFilename(),
                    "size", photo.getFileSize()
            ));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error al subir la foto: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/{teacherId}/photo")
    public ResponseEntity<byte[]> getPhoto(@PathVariable Long teacherId) {
        byte[] imageData = profilePhotoService.getPhotoData(teacherId);

        if (imageData == null) {
            return ResponseEntity.notFound().build();
        }

        String contentType = profilePhotoService.getPhotoContentType(teacherId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(imageData.length);
        headers.setCacheControl(CacheControl.maxAge(java.time.Duration.ofDays(7)));

        return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
    }

    @GetMapping("/{teacherId}/photo/info")
    public ResponseEntity<Map<String, Object>> getPhotoInfo(@PathVariable Long teacherId) {
        return profilePhotoService.getPhoto(teacherId)
                .map(photo -> ResponseEntity.ok(Map.of(
                        "exists", true,
                        "photoId", photo.getId(),
                        "filename", photo.getFilename(),
                        "contentType", photo.getContentType(),
                        "size", photo.getFileSize(),
                        "uploadedAt", photo.getUploadedAt().toString()
                )))
                .orElseGet(() -> ResponseEntity.ok(Map.of("exists", false)));
    }

    @DeleteMapping("/{teacherId}/photo")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'TEACHER')")
    public ResponseEntity<Map<String, Object>> deletePhoto(@PathVariable Long teacherId) {
        profilePhotoService.deletePhoto(teacherId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Foto eliminada correctamente"
        ));
    }

    @GetMapping("/{teacherId}/photo/exists")
    public ResponseEntity<Map<String, Boolean>> hasPhoto(@PathVariable Long teacherId) {
        return ResponseEntity.ok(Map.of("hasPhoto", profilePhotoService.hasPhoto(teacherId)));
    }
}

