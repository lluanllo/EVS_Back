# Script para levantar todos los microservicios
$JAVA_HOME = "C:\Users\jjace\.jdks\ms-21.0.9"
$env:JAVA_HOME = $JAVA_HOME
$env:PATH = "$JAVA_HOME\bin;$env:PATH"

Write-Host "=== Iniciando servicios de infraestructura (Docker) ===" -ForegroundColor Green
docker-compose up -d

Write-Host "`n=== Esperando 10 segundos para que Docker se estabilice ===" -ForegroundColor Yellow
Start-Sleep -Seconds 10

Write-Host "`n=== Compilando todos los microservicios ===" -ForegroundColor Green

# Eureka Server
Write-Host "`nCompilando Eureka Server..." -ForegroundColor Cyan
cd MicroserviceErekaServer
.\mvnw.cmd clean package -DskipTests
if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Eureka Server compilado" -ForegroundColor Green
    Start-Process -NoNewWindow -FilePath "$JAVA_HOME\bin\java.exe" -ArgumentList "-jar", "target\MicroserviceErekaServer-0.0.1-SNAPSHOT.jar"
    Write-Host "✓ Eureka Server iniciado en puerto 8761" -ForegroundColor Green
    Start-Sleep -Seconds 15
} else {
    Write-Host "✗ Error compilando Eureka Server" -ForegroundColor Red
}
cd ..

# Contability API
Write-Host "`nCompilando Contability API..." -ForegroundColor Cyan
cd MicroserviceContabilityApi
.\mvnw.cmd clean package -DskipTests
if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Contability API compilado" -ForegroundColor Green
    Start-Process -NoNewWindow -FilePath "$JAVA_HOME\bin\java.exe" -ArgumentList "-jar", "target\MicroserviceContabilityApi-0.0.1-SNAPSHOT.jar"
    Write-Host "✓ Contability API iniciado en puerto 8084" -ForegroundColor Green
    Start-Sleep -Seconds 5
} else {
    Write-Host "✗ Error compilando Contability API" -ForegroundColor Red
}
cd ..

# Regatta API
Write-Host "`nCompilando Regatta API..." -ForegroundColor Cyan
cd MicroserviceRegattaApi
.\mvnw.cmd clean package -DskipTests
if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Regatta API compilado" -ForegroundColor Green
    Start-Process -NoNewWindow -FilePath "$JAVA_HOME\bin\java.exe" -ArgumentList "-jar", "target\MicroserviceRegattaApi-0.0.1-SNAPSHOT.jar"
    Write-Host "✓ Regatta API iniciado en puerto 8085" -ForegroundColor Green
    Start-Sleep -Seconds 5
} else {
    Write-Host "✗ Error compilando Regatta API" -ForegroundColor Red
}
cd ..

# Course API
Write-Host "`nCompilando Course API..." -ForegroundColor Cyan
cd MicroserviceCourseApi
.\mvnw.cmd clean package -DskipTests
if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Course API compilado" -ForegroundColor Green
    Start-Process -NoNewWindow -FilePath "$JAVA_HOME\bin\java.exe" -ArgumentList "-jar", "target\MicroserviceCourseApi-0.0.1-SNAPSHOT.jar"
    Write-Host "✓ Course API iniciado en puerto 8083" -ForegroundColor Green
    Start-Sleep -Seconds 5
} else {
    Write-Host "✗ Error compilando Course API" -ForegroundColor Red
}
cd ..

# Teacher API
Write-Host "`nCompilando Teacher API..." -ForegroundColor Cyan
cd MicroserviceTeacherRegisterAPI
.\mvnw.cmd clean package -DskipTests
if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Teacher API compilado" -ForegroundColor Green
    Start-Process -NoNewWindow -FilePath "$JAVA_HOME\bin\java.exe" -ArgumentList "-jar", "target\MicroserviceTeacherRegisterAPI-0.0.1-SNAPSHOT.jar"
    Write-Host "✓ Teacher API iniciado en puerto 8082" -ForegroundColor Green
    Start-Sleep -Seconds 5
} else {
    Write-Host "✗ Error compilando Teacher API" -ForegroundColor Red
}
cd ..

# Students API
Write-Host "`nCompilando Students API..." -ForegroundColor Cyan
cd MicroserviceStudentsAPI
.\mvnw.cmd clean package -DskipTests
if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Students API compilado" -ForegroundColor Green
    Start-Process -NoNewWindow -FilePath "$JAVA_HOME\bin\java.exe" -ArgumentList "-jar", "target\MicroserviceStudentsAPI-0.0.1-SNAPSHOT.jar"
    Write-Host "✓ Students API iniciado en puerto 8081" -ForegroundColor Green
} else {
    Write-Host "✗ Error compilando Students API" -ForegroundColor Red
}
cd ..

Write-Host "`n=== Todos los servicios iniciados ===" -ForegroundColor Green
Write-Host "`nServicios disponibles:"
Write-Host "- Eureka Server: http://localhost:8761" -ForegroundColor Cyan
Write-Host "- Students API: http://localhost:8081" -ForegroundColor Cyan
Write-Host "- Teacher API: http://localhost:8082" -ForegroundColor Cyan
Write-Host "- Course API: http://localhost:8083" -ForegroundColor Cyan
Write-Host "- Contability API: http://localhost:8084" -ForegroundColor Cyan
Write-Host "- Regatta API: http://localhost:8085" -ForegroundColor Cyan
Write-Host "- Kafka UI: http://localhost:8090" -ForegroundColor Cyan
Write-Host "- Mongo Express: http://localhost:8091" -ForegroundColor Cyan

Write-Host "`nPresiona Ctrl+C para detener todos los servicios" -ForegroundColor Yellow
Wait-Event

