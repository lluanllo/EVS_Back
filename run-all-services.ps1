# Script completo para compilar y ejecutar todos los microservicios
$ErrorActionPreference = "Continue"
$JAVA_HOME = "C:\Users\jjace\.jdks\ms-21.0.9"
$env:JAVA_HOME = $JAVA_HOME
$env:PATH = "$JAVA_HOME\bin;$env:PATH"

$baseDir = "C:\Users\jjace\Desktop\dev\evs\EVS_Back"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   COMPILANDO Y EJECUTANDO MICROSERVICIOS" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Función para compilar un microservicio
function Compile-Service {
    param(
        [string]$serviceName,
        [string]$serviceDir
    )

    Write-Host "Compilando $serviceName..." -ForegroundColor Yellow
    Push-Location "$baseDir\$serviceDir"

    & .\mvnw.cmd clean package -DskipTests 2>&1 | Out-Null

    if ($LASTEXITCODE -eq 0) {
        Write-Host "✓ $serviceName compilado exitosamente" -ForegroundColor Green
        Pop-Location
        return $true
    } else {
        Write-Host "✗ Error compilando $serviceName" -ForegroundColor Red
        Pop-Location
        return $false
    }
}

# Función para iniciar un microservicio
function Start-Service {
    param(
        [string]$serviceName,
        [string]$serviceDir,
        [string]$jarName,
        [int]$waitSeconds = 10
    )

    Write-Host "Iniciando $serviceName..." -ForegroundColor Yellow
    $jarPath = "$baseDir\$serviceDir\target\$jarName"

    if (Test-Path $jarPath) {
        Start-Process -FilePath "$JAVA_HOME\bin\java.exe" `
                     -ArgumentList "-jar", $jarPath `
                     -WorkingDirectory "$baseDir\$serviceDir" `
                     -WindowStyle Normal

        Write-Host "✓ $serviceName iniciado" -ForegroundColor Green
        Write-Host "  Esperando $waitSeconds segundos..." -ForegroundColor Gray
        Start-Sleep -Seconds $waitSeconds
        return $true
    } else {
        Write-Host "✗ JAR no encontrado: $jarPath" -ForegroundColor Red
        return $false
    }
}

# 1. Compilar Eureka Server
Write-Host "`n[1/6] EUREKA SERVER" -ForegroundColor Cyan
if (Compile-Service "Eureka Server" "MicroserviceErekaServer") {
    Start-Service "Eureka Server" "MicroserviceErekaServer" "MicroserviceErekaServer-0.0.1-SNAPSHOT.jar" 20
}

# 2. Compilar Contability API
Write-Host "`n[2/6] CONTABILITY API" -ForegroundColor Cyan
if (Compile-Service "Contability API" "MicroserviceContabilityApi") {
    Start-Service "Contability API" "MicroserviceContabilityApi" "MicroserviceContabilityApi-0.0.1-SNAPSHOT.jar" 10
}

# 3. Compilar Regatta API
Write-Host "`n[3/6] REGATTA API" -ForegroundColor Cyan
if (Compile-Service "Regatta API" "MicroserviceRegattaApi") {
    Start-Service "Regatta API" "MicroserviceRegattaApi" "MicroserviceRegattaApi-0.0.1-SNAPSHOT.jar" 10
}

# 4. Compilar Course API
Write-Host "`n[4/6] COURSE API" -ForegroundColor Cyan
if (Compile-Service "Course API" "MicroserviceCourseApi") {
    Start-Service "Course API" "MicroserviceCourseApi" "MicroserviceCourseApi-0.0.1-SNAPSHOT.jar" 10
}

# 5. Compilar Teacher API
Write-Host "`n[5/6] TEACHER API" -ForegroundColor Cyan
if (Compile-Service "Teacher API" "MicroserviceTeacherRegisterAPI") {
    Start-Service "Teacher API" "MicroserviceTeacherRegisterAPI" "MicroserviceTeacherRegisterAPI-0.0.1-SNAPSHOT.jar" 10
}

# 6. Compilar Students API
Write-Host "`n[6/6] STUDENTS API" -ForegroundColor Cyan
if (Compile-Service "Students API" "MicroserviceStudentsAPI") {
    Start-Service "Students API" "MicroserviceStudentsAPI" "MicroserviceStudentsAPI-0.0.1-SNAPSHOT.jar" 5
}

Write-Host "`n========================================" -ForegroundColor Green
Write-Host "   TODOS LOS SERVICIOS INICIADOS" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green

Write-Host "`nServicios disponibles:" -ForegroundColor Cyan
Write-Host "  - Eureka Server:    http://localhost:8761" -ForegroundColor White
Write-Host "  - Students API:     http://localhost:8081" -ForegroundColor White
Write-Host "  - Teacher API:      http://localhost:8082" -ForegroundColor White
Write-Host "  - Course API:       http://localhost:8083" -ForegroundColor White
Write-Host "  - Contability API:  http://localhost:8084" -ForegroundColor White
Write-Host "  - Regatta API:      http://localhost:8085" -ForegroundColor White

Write-Host "`nInfraestructura Docker:" -ForegroundColor Cyan
Write-Host "  - PostgreSQL:       localhost:5432" -ForegroundColor White
Write-Host "  - MongoDB:          localhost:27017" -ForegroundColor White
Write-Host "  - Kafka:            localhost:29092" -ForegroundColor White
Write-Host "  - Kafka UI:         http://localhost:8090" -ForegroundColor White
Write-Host "  - Mongo Express:    http://localhost:8091" -ForegroundColor White

Write-Host "`nPresiona cualquier tecla para salir..." -ForegroundColor Yellow
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

