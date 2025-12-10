package com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Foto de perfil del profesor almacenada en MongoDB
 */
@Data
@Builder
@Document(collection = "profile_photos")
@AllArgsConstructor
@NoArgsConstructor
public class ProfilePhoto {

    @Id
    private String id;

    private Long teacherId;

    private byte[] imageData;

    private String contentType;

    private String filename;

    private Long fileSize;

    private LocalDateTime uploadedAt;

    private LocalDateTime updatedAt;
}

