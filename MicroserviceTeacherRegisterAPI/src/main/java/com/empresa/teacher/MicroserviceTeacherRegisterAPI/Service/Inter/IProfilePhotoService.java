package com.empresa.teacher.MicroserviceTeacherRegisterAPI.Service.Inter;

import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Mongo.ProfilePhoto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface IProfilePhotoService {

    ProfilePhoto uploadPhoto(Long teacherId, MultipartFile file) throws IOException;

    Optional<ProfilePhoto> getPhoto(Long teacherId);

    byte[] getPhotoData(Long teacherId);

    String getPhotoContentType(Long teacherId);

    void deletePhoto(Long teacherId);

    boolean hasPhoto(Long teacherId);
}

