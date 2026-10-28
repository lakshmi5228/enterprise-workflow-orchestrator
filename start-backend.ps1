# ============================================================
# start-backend.ps1  --  Launch the Enterprise Workflow Orchestrator backend
# ============================================================
# Prerequisites:
#   - Java 17+ installed (JDK 25 works fine)
#   - MySQL 8 running on localhost:3306 with user 'root'
#   - Database 'yottaflex_db' will be auto-created on first run
#   - Synthetic data auto-seeded from synthetic_data/exports/ on first run
#
# Usage:
#   .\start-backend.ps1                      # default: MySQL mode
#   .\start-backend.ps1 -Profile h2          # in-memory H2 (no MySQL needed)
#   .\start-backend.ps1 -Profile h2 -Port 9090
# ============================================================

param(
    [string]$Profile = "default",
    [int]$Port = 8080
)

$JAVA_HOME_PATH = "C:\Program Files\Java\jdk-25.0.3"
$JAR_PATH       = "$PSScriptRoot\backend\target\orchestrator-1.0.0.jar"

# Fallback: build on the fly if JAR doesn't exist
if (-not (Test-Path $JAR_PATH)) {
    Write-Host "[*] JAR not found. Building backend first..." -ForegroundColor Yellow
    $mvn = "$PSScriptRoot\tools\maven\apache-maven-3.9.6\bin\mvn.cmd"
    $env:JAVA_HOME = $JAVA_HOME_PATH
    & $mvn -f "$PSScriptRoot\backend\pom.xml" package -DskipTests -q
    if ($LASTEXITCODE -ne 0) {
        Write-Host "[-] Build failed. Exiting." -ForegroundColor Red
        exit 1
    }
    Write-Host "[+] Build successful." -ForegroundColor Green
}

Write-Host ""
Write-Host "=================================================" -ForegroundColor Cyan
Write-Host " Enterprise Workflow Orchestrator - Backend API  " -ForegroundColor Cyan
Write-Host "=================================================" -ForegroundColor Cyan
Write-Host " Profile : $Profile"
Write-Host " Port    : $Port"
Write-Host " JAR     : $JAR_PATH"
Write-Host ""
Write-Host " API Base   : http://localhost:$Port"
Write-Host " H2 Console : http://localhost:$Port/h2-console  (h2 profile only)"
Write-Host "=================================================" -ForegroundColor Cyan
Write-Host ""

$javaExe = "$JAVA_HOME_PATH\bin\java.exe"

if ($Profile -eq "h2") {
    & $javaExe -jar $JAR_PATH --spring.profiles.active=h2 --server.port=$Port
} else {
    & $javaExe -jar $JAR_PATH --server.port=$Port
}

