package com.empresa.course.MicroserviceCourseApi.Service.Inter;

import com.empresa.course.MicroserviceCourseApi.Entities.RoutePlan;

/**
 * Interfaz para la generación de imágenes de rutas (Single Responsibility)
 */
public interface IRouteImageService {

    byte[] generateRouteImage(RoutePlan routePlan);

    byte[] generateRouteImageWithBackground(RoutePlan routePlan, byte[] backgroundImage);

    String saveRouteImage(RoutePlan routePlan);
}

