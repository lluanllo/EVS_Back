@echo off
setlocal

set JAVA_HOME=C:\Users\jjace\.jdks\ms-21.0.9
set PATH=%JAVA_HOME%\bin;%PATH%

echo ============================================
echo Iniciando todos los microservicios...
echo ============================================

echo.
echo Iniciando Eureka Server (puerto 8761)...
cd MicroserviceErekaServer
start "Eureka Server" java -jar target\MicroserviceErekaServer-0.0.1-SNAPSHOT.jar
timeout /t 20 /nobreak
cd ..

echo.
echo Iniciando Contability API (puerto 8084)...
cd MicroserviceContabilityApi
start "Contability API" java -jar target\MicroserviceContabilityApi-0.0.1-SNAPSHOT.jar
timeout /t 10 /nobreak
cd ..

echo.
echo Iniciando Regatta API (puerto 8085)...
cd MicroserviceRegattaApi
start "Regatta API" java -jar target\MicroserviceRegattaApi-0.0.1-SNAPSHOT.jar
timeout /t 10 /nobreak
cd ..

echo.
echo Iniciando Course API (puerto 8083)...
cd MicroserviceCourseApi
start "Course API" java -jar target\MicroserviceCourseApi-0.0.1-SNAPSHOT.jar
timeout /t 10 /nobreak
cd ..

echo.
echo Iniciando Teacher API (puerto 8082)...
cd MicroserviceTeacherRegisterAPI
start "Teacher API" java -jar target\MicroserviceTeacherRegisterAPI-0.0.1-SNAPSHOT.jar
timeout /t 10 /nobreak
cd ..

echo.
echo Iniciando Students API (puerto 8081)...
cd MicroserviceStudentsAPI
start "Students API" java -jar target\MicroserviceStudentsAPI-0.0.1-SNAPSHOT.jar
cd ..

echo.
echo ============================================
echo Todos los servicios iniciados!
echo ============================================
echo.
echo Servicios disponibles:
echo - Eureka Server: http://localhost:8761
echo - Students API: http://localhost:8081
echo - Teacher API: http://localhost:8082
echo - Course API: http://localhost:8083
echo - Contability API: http://localhost:8084
echo - Regatta API: http://localhost:8085
echo.
echo Infraestructura Docker:
echo - PostgreSQL: localhost:5432
echo - MongoDB: localhost:27017
echo - Kafka: localhost:29092
echo - Kafka UI: http://localhost:8090
echo - Mongo Express: http://localhost:8091
echo.
pause

