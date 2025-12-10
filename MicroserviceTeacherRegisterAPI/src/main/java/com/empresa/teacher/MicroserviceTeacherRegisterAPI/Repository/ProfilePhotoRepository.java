package com.empresa.teacher.MicroserviceTeacherRegisterAPI.Repository;

import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities.Mongo.ProfilePhoto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfilePhotoRepository extends MongoRepository<ProfilePhoto, String> {

    Optional<ProfilePhoto> findByTeacherId(Long teacherId);

    void deleteByTeacherId(Long teacherId);

    boolean existsByTeacherId(Long teacherId);
}

