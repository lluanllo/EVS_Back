# Script para verificar el estado de todos los microservicios
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   VERIFICANDO ESTADO DE SERVICIOS" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Función para verificar si un puerto está en uso
function Test-Port {
    param([int]$port)
    $connection = Test-NetConnection -ComputerName localhost -Port $port -WarningAction SilentlyContinue -InformationLevel Quiet
    return $connection
}

# Función para hacer una petición HTTP simple
function Test-HttpEndpoint {
    param([string]$url)
    try {
        $response = Invoke-WebRequest -Uri $url -TimeoutSec 2 -UseBasicParsing -ErrorAction SilentlyContinue
        return $response.StatusCode -eq 200
    } catch {
        return $false
    }
}

# Verificar servicios
$services = @(
    @{Name="Eureka Server"; Port=8761; Url="http://localhost:8761"},
    @{Name="Students API"; Port=8081; Url="http://localhost:8081/actuator/health"},
    @{Name="Teacher API"; Port=8082; Url="http://localhost:8082/actuator/health"},
    @{Name="Course API"; Port=8083; Url="http://localhost:8083/actuator/health"},
    @{Name="Contability API"; Port=8084; Url="http://localhost:8084/actuator/health"},
    @{Name="Regatta API"; Port=8085; Url="http://localhost:8085/actuator/health"}
)

Write-Host "Microservicios:" -ForegroundColor Yellow
foreach ($service in $services) {
    Write-Host -NoNewline "  $($service.Name.PadRight(20)) "

    if (Test-Port $service.Port) {
        Write-Host "✓ RUNNING (puerto $($service.Port))" -ForegroundColor Green
    } else {
        Write-Host "✗ NOT RUNNING" -ForegroundColor Red
    }
}

Write-Host "`nInfraestructura Docker:" -ForegroundColor Yellow
$dockerServices = @(
    @{Name="PostgreSQL"; Port=5432},
    @{Name="MongoDB"; Port=27017},
    @{Name="Kafka"; Port=29092},
    @{Name="Kafka UI"; Port=8090},
    @{Name="Mongo Express"; Port=8091}
)

foreach ($service in $dockerServices) {
    Write-Host -NoNewline "  $($service.Name.PadRight(20)) "

    if (Test-Port $service.Port) {
        Write-Host "✓ RUNNING (puerto $($service.Port))" -ForegroundColor Green
    } else {
        Write-Host "✗ NOT RUNNING" -ForegroundColor Red
    }
}

Write-Host "`nProcesos Java activos:" -ForegroundColor Yellow
$javaProcesses = Get-Process java -ErrorAction SilentlyContinue
if ($javaProcesses) {
    Write-Host "  Total: $($javaProcesses.Count) proceso(s)" -ForegroundColor Green
    $javaProcesses | ForEach-Object {
        $memoryMB = [math]::Round($_.WorkingSet64 / 1MB, 2)
        Write-Host "  - PID $($_.Id): ${memoryMB} MB" -ForegroundColor Gray
    }
} else {
    Write-Host "  No hay procesos Java en ejecución" -ForegroundColor Red
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host ""

