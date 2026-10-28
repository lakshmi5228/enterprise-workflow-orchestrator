# ============================================================
# start-all.ps1  --  Start both backend and frontend together
# ============================================================
# Opens two PowerShell windows: one for the backend, one for the frontend.
# Requires the JAR to already exist (run start-backend.ps1 once to build it).
# ============================================================

param(
    [string]$BackendProfile = "h2",   # use 'default' for MySQL
    [int]$BackendPort = 8080,
    [int]$FrontendPort = 3000
)

$rootDir = $PSScriptRoot

Write-Host "[*] Starting backend in new window (profile=$BackendProfile, port=$BackendPort)..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "& '$rootDir\start-backend.ps1' -Profile $BackendProfile -Port $BackendPort"

Start-Sleep -Seconds 4

Write-Host "[*] Starting frontend in new window (port=$FrontendPort)..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "& '$rootDir\start-frontend.ps1' -Port $FrontendPort"

Write-Host ""
Write-Host "[+] Both services launched." -ForegroundColor Green
Write-Host "    Backend  : http://localhost:$BackendPort"
Write-Host "    Frontend : http://localhost:$FrontendPort"
Write-Host ""
Write-Host "    Close the individual PowerShell windows to stop the services."

