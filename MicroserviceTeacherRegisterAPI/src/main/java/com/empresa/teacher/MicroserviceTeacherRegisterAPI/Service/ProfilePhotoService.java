package com.empresa.teacher.MicroserviceTeacherRegisterAPI.Service;

import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Mongo.ProfilePhoto;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Repository.ProfilePhotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfilePhotoService {

    private final ProfilePhotoRepository photoRepository;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_TYPES = {"image/jpeg", "image/png", "image/gif", "image/webp"};

    @Transactional
    public ProfilePhoto uploadPhoto(Long teacherId, MultipartFile file) throws IOException {
        // Validar archivo
        validateFile(file);

        // Eliminar foto anterior si existe
        photoRepository.findByTeacherId(teacherId).ifPresent(existing -> {
            photoRepository.delete(existing);
            log.info("Foto anterior eliminada para profesor {}", teacherId);
        });

        ProfilePhoto photo = ProfilePhoto.builder()
                .teacherId(teacherId)
                .imageData(file.getBytes())
                .contentType(file.getContentType())
                .filename(file.getOriginalFilename())
                .fileSize(file.getSize())
                .uploadedAt(LocalDateTime.now())
                .build();

        ProfilePhoto saved = photoRepository.save(photo);
        log.info("Foto de perfil subida para profesor {}: {} bytes", teacherId, file.getSize());

        return saved;
    }

    public Optional<ProfilePhoto> getPhoto(Long teacherId) {
        return photoRepository.findByTeacherId(teacherId);
    }

    public byte[] getPhotoData(Long teacherId) {
        return photoRepository.findByTeacherId(teacherId)
                .map(ProfilePhoto::getImageData)
                .orElse(null);
    }

    public String getPhotoContentType(Long teacherId) {
        return photoRepository.findByTeacherId(teacherId)
                .map(ProfilePhoto::getContentType)
                .orElse("image/jpeg");
    }

    @Transactional
    public void deletePhoto(Long teacherId) {
        photoRepository.findByTeacherId(teacherId).ifPresent(photo -> {
            photoRepository.delete(photo);
            log.info("Foto de perfil eliminada para profesor {}", teacherId);
        });
    }

    public boolean hasPhoto(Long teacherId) {
        return photoRepository.existsByTeacherId(teacherId);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("El archivo está vacío");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("El archivo excede el tamaño máximo de 5MB");
        }

        String contentType = file.getContentType();
        boolean validType = false;
        for (String allowed : ALLOWED_TYPES) {
            if (allowed.equals(contentType)) {
                validType = true;
                break;
            }
        }

        if (!validType) {
            throw new RuntimeException("Tipo de archivo no permitido. Use: JPEG, PNG, GIF o WebP");
        }
    }
}

