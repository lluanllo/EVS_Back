# Script simplificado para compilar y levantar microservicios
$ErrorActionPreference = "Continue"
$JAVA_HOME = "C:\Users\jjace\.jdks\ms-21.0.9"
$env:JAVA_HOME = $JAVA_HOME
$env:PATH = "$JAVA_HOME\bin;$env:PATH"

Write-Host "=== INICIANDO MICROSERVICIOS EVS ===" -ForegroundColor Green
Write-Host "JAVA_HOME: $JAVA_HOME" -ForegroundColor Cyan

# 1. Eureka Server
Write-Host "`n[1/6] Iniciando Eureka Server (puerto 8761)..." -ForegroundColor Yellow
cd MicroserviceErekaServer
Start-Process -FilePath "$JAVA_HOME\bin\java.exe" -ArgumentList "-jar", "target\MicroserviceErekaServer-0.0.1-SNAPSHOT.jar" -WindowStyle Hidden
Start-Sleep -Seconds 20
Write-Host "✓ Eureka Server iniciado" -ForegroundColor Green
cd ..

# 2. Contability API
Write-Host "`n[2/6] Iniciando Contability API (puerto 8084)..." -ForegroundColor Yellow
cd MicroserviceContabilityApi
$process = Start-Process -FilePath ".\mvnw.cmd" -ArgumentList "clean", "package", "-DskipTests" -PassThru -Wait -NoNewWindow
if ($process.ExitCode -eq 0) {
    Start-Process -FilePath "$JAVA_HOME\bin\java.exe" -ArgumentList "-jar", "target\MicroserviceContabilityApi-0.0.1-SNAPSHOT.jar" -WindowStyle Hidden
    Write-Host "✓ Contability API iniciado" -ForegroundColor Green
} else {
    Write-Host "✗ Error compilando Contability API" -ForegroundColor Red
}
cd ..
Start-Sleep -Seconds 10

# 3. Regatta API
Write-Host "`n[3/6] Iniciando Regatta API (puerto 8085)..." -ForegroundColor Yellow
cd MicroserviceRegattaApi
$process = Start-Process -FilePath ".\mvnw.cmd" -ArgumentList "clean", "package", "-DskipTests" -PassThru -Wait -NoNewWindow
if ($process.ExitCode -eq 0) {
    Start-Process -FilePath "$JAVA_HOME\bin\java.exe" -ArgumentList "-jar", "target\MicroserviceRegattaApi-0.0.1-SNAPSHOT.jar" -WindowStyle Hidden
    Write-Host "✓ Regatta API iniciado" -ForegroundColor Green
} else {
    Write-Host "✗ Error compilando Regatta API" -ForegroundColor Red
}
cd ..
Start-Sleep -Seconds 10

# 4. Course API
Write-Host "`n[4/6] Iniciando Course API (puerto 8083)..." -ForegroundColor Yellow
cd MicroserviceCourseApi
$process = Start-Process -FilePath ".\mvnw.cmd" -ArgumentList "clean", "package", "-DskipTests" -PassThru -Wait -NoNewWindow
if ($process.ExitCode -eq 0) {
    Start-Process -FilePath "$JAVA_HOME\bin\java.exe" -ArgumentList "-jar", "target\MicroserviceCourseApi-0.0.1-SNAPSHOT.jar" -WindowStyle Hidden
    Write-Host "✓ Course API iniciado" -ForegroundColor Green
} else {
    Write-Host "✗ Error compilando Course API" -ForegroundColor Red
}
cd ..
Start-Sleep -Seconds 10

# 5. Teacher API
Write-Host "`n[5/6] Iniciando Teacher API (puerto 8082)..." -ForegroundColor Yellow
cd MicroserviceTeacherRegisterAPI
$process = Start-Process -FilePath ".\mvnw.cmd" -ArgumentList "clean", "package", "-DskipTests" -PassThru -Wait -NoNewWindow
if ($process.ExitCode -eq 0) {
    Start-Process -FilePath "$JAVA_HOME\bin\java.exe" -ArgumentList "-jar", "target\MicroserviceTeacherRegisterAPI-0.0.1-SNAPSHOT.jar" -WindowStyle Hidden
    Write-Host "✓ Teacher API iniciado" -ForegroundColor Green
} else {
    Write-Host "✗ Error compilando Teacher API" -ForegroundColor Red
}
cd ..
Start-Sleep -Seconds 10

# 6. Students API
Write-Host "`n[6/6] Iniciando Students API (puerto 8081)..." -ForegroundColor Yellow
cd MicroserviceStudentsAPI
$process = Start-Process -FilePath ".\mvnw.cmd" -ArgumentList "clean", "package", "-DskipTests" -PassThru -Wait -NoNewWindow
if ($process.ExitCode -eq 0) {
    Start-Process -FilePath "$JAVA_HOME\bin\java.exe" -ArgumentList "-jar", "target\MicroserviceStudentsAPI-0.0.1-SNAPSHOT.jar" -WindowStyle Hidden
    Write-Host "✓ Students API iniciado" -ForegroundColor Green
} else {
    Write-Host "✗ Error compilando Students API" -ForegroundColor Red
}
cd ..

Write-Host "`n=== TODOS LOS SERVICIOS INICIADOS ===" -ForegroundColor Green
Write-Host "`nServicios disponibles:"
Write-Host "- Eureka Server: http://localhost:8761" -ForegroundColor Cyan
Write-Host "- Students API: http://localhost:8081" -ForegroundColor Cyan
Write-Host "- Teacher API: http://localhost:8082" -ForegroundColor Cyan
Write-Host "- Course API: http://localhost:8083" -ForegroundColor Cyan
Write-Host "- Contability API: http://localhost:8084" -ForegroundColor Cyan
Write-Host "- Regatta API: http://localhost:8085" -ForegroundColor Cyan
Write-Host "`nInfraestructura Docker:"
Write-Host "- PostgreSQL: localhost:5432" -ForegroundColor Cyan
Write-Host "- MongoDB: localhost:27017" -ForegroundColor Cyan
Write-Host "- Kafka: localhost:29092" -ForegroundColor Cyan
Write-Host "- Kafka UI: http://localhost:8090" -ForegroundColor Cyan
Write-Host "- Mongo Express: http://localhost:8091" -ForegroundColor Cyan

Write-Host "`nPresiona cualquier tecla para salir..." -ForegroundColor Yellow
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

