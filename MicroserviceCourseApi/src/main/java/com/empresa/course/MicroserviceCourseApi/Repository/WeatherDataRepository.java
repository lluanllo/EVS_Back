package com.empresa.course.MicroserviceCourseApi.Repository;

import com.empresa.course.MicroserviceCourseApi.Entities.WeatherData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeatherDataRepository extends MongoRepository<WeatherData, String> {

    Optional<WeatherData> findTopByOrderByTimestampDesc();

    List<WeatherData> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime start, LocalDateTime end);

    @Query("{ 'timestamp': { $gte: ?0 } }")
    List<WeatherData> findByTimestampAfter(LocalDateTime timestamp);

    List<WeatherData> findTop24ByOrderByTimestampDesc();

    List<WeatherData> findTop168ByOrderByTimestampDesc(); // Últimos 7 días (24*7)
}

