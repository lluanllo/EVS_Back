# Script para detener todos los microservicios
Write-Host "========================================" -ForegroundColor Red
Write-Host "   DETENIENDO MICROSERVICIOS" -ForegroundColor Red
Write-Host "========================================" -ForegroundColor Red
Write-Host ""

# Obtener todos los procesos Java
$javaProcesses = Get-Process java -ErrorAction SilentlyContinue

if ($javaProcesses) {
    Write-Host "Se encontraron $($javaProcesses.Count) proceso(s) Java en ejecución" -ForegroundColor Yellow
    Write-Host ""

    foreach ($process in $javaProcesses) {
        try {
            Write-Host "Deteniendo proceso Java (PID: $($process.Id))..." -ForegroundColor Yellow
            Stop-Process -Id $process.Id -Force
            Write-Host "✓ Proceso $($process.Id) detenido" -ForegroundColor Green
        } catch {
            Write-Host "✗ Error deteniendo proceso $($process.Id): $_" -ForegroundColor Red
        }
    }

    Write-Host ""
    Write-Host "✓ Todos los microservicios han sido detenidos" -ForegroundColor Green
} else {
    Write-Host "No se encontraron procesos Java en ejecución" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Para detener Docker Compose, ejecuta:" -ForegroundColor Cyan
Write-Host "  docker-compose down" -ForegroundColor White
Write-Host ""

pause

