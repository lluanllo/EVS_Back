package com.empresa.course.MicroserviceCourseApi.Controller;


import com.empresa.course.MicroserviceCourseApi.Service.Impl.ScheudleServiceImpl;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/schedules")
public class ScheudleController {

    @Autowired
    private ScheudleServiceImpl scheudleServiceImpl;

}
