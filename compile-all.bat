@echo off
setlocal

set JAVA_HOME=C:\Users\jjace\.jdks\ms-21.0.9
set PATH=%JAVA_HOME%\bin;%PATH%

echo ============================================
echo Compilando todos los microservicios...
echo ============================================

echo.
echo [1/6] Compilando Eureka Server...
cd MicroserviceErekaServer
call mvnw.cmd clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Fallo al compilar Eureka Server
) else (
    echo OK: Eureka Server compilado
)
cd ..

echo.
echo [2/6] Compilando Contability API...
cd MicroserviceContabilityApi
call mvnw.cmd clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Fallo al compilar Contability API
) else (
    echo OK: Contability API compilado
)
cd ..

echo.
echo [3/6] Compilando Regatta API...
cd MicroserviceRegattaApi
call mvnw.cmd clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Fallo al compilar Regatta API
) else (
    echo OK: Regatta API compilado
)
cd ..

echo.
echo [4/6] Compilando Course API...
cd MicroserviceCourseApi
call mvnw.cmd clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Fallo al compilar Course API
) else (
    echo OK: Course API compilado
)
cd ..

echo.
echo [5/6] Compilando Teacher API...
cd MicroserviceTeacherRegisterAPI
call mvnw.cmd clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Fallo al compilar Teacher API
) else (
    echo OK: Teacher API compilado
)
cd ..

echo.
echo [6/6] Compilando Students API...
cd MicroserviceStudentsAPI
call mvnw.cmd clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Fallo al compilar Students API
) else (
    echo OK: Students API compilado
)
cd ..

echo.
echo ============================================
echo Compilacion completada!
echo ============================================
pause

