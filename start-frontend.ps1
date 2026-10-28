# ============================================================
# start-frontend.ps1  --  Serve the React SPA frontend
# ============================================================
# Serves the pre-built React production bundle on port 3000.
# Make sure the backend is running first (default port 8080).
#
# Usage:
#   .\start-frontend.ps1              # serves on http://localhost:3000
#   .\start-frontend.ps1 -Port 4000
# ============================================================

param(
    [int]$Port = 3000
)

$BUILD_DIR = "$PSScriptRoot\frontend\build"

if (-not (Test-Path "$BUILD_DIR\index.html")) {
    Write-Host "[!] Production build not found at $BUILD_DIR" -ForegroundColor Yellow
    Write-Host "[*] Building frontend now..." -ForegroundColor Yellow
    Set-Location "$PSScriptRoot\frontend"
    $env:DISABLE_ESLINT_PLUGIN = "true"
    $env:REACT_APP_API_BASE_URL = "http://localhost:8080"
    npm run build
    if ($LASTEXITCODE -ne 0) {
        Write-Host "[-] Frontend build failed. Exiting." -ForegroundColor Red
        exit 1
    }
}

Write-Host ""
Write-Host "=================================================" -ForegroundColor Cyan
Write-Host " Enterprise Workflow Orchestrator - Frontend     " -ForegroundColor Cyan
Write-Host "=================================================" -ForegroundColor Cyan
Write-Host " Serving build directory : $BUILD_DIR"
Write-Host " URL : http://localhost:$Port"
Write-Host "=================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "[*] Open your browser at http://localhost:$Port" -ForegroundColor Green
Write-Host "[*] Press Ctrl+C to stop." -ForegroundColor Green
Write-Host ""

# Use npx serve (bundled with react-scripts) to host the static build
Set-Location "$PSScriptRoot\frontend"
npx.cmd serve -s build -l $Port

