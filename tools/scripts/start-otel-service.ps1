<#
.SYNOPSIS
    Manages the local Grafana LGTM container for OpenTelemetry traces, metrics, and logs.
.DESCRIPTION
    Starts, stops, and monitors the Grafana LGTM container (OTel-native observability stack).
.NOTES
    Grafana UI    : http://localhost:3000
    OTLP gRPC     : localhost:4317
    OTLP HTTP     : localhost:4318
    Requires OTel exporter config in application.properties — already included by default.
.PARAMETER Action
    up     : Start Grafana LGTM container.
    down   : Stop Grafana LGTM container.
    status : Show Grafana LGTM container status.
#>
param (
    [ValidateSet("up", "down", "status")]
    [string]$Action = "up"
)

$ErrorActionPreference = "Stop"
$ComposeFile = (Join-Path $PSScriptRoot "docker-compose.dev.yaml")

function Invoke-Up {
    Write-Host "`n=== Starting Grafana LGTM (OTel) ===" -ForegroundColor Cyan
    docker compose -f $ComposeFile --profile telemetry up -d grafana-lgtm
    Write-Host "Grafana LGTM is starting." -ForegroundColor Green
    Write-Host "  Grafana UI : http://localhost:3000" -ForegroundColor DarkGray
    Write-Host "  OTLP gRPC  : localhost:4317" -ForegroundColor DarkGray
    Write-Host "  OTLP HTTP  : localhost:4318" -ForegroundColor DarkGray
}

function Invoke-Down {
    Write-Host "Stopping Grafana LGTM..." -ForegroundColor Yellow
    docker compose -f $ComposeFile --profile telemetry stop grafana-lgtm 2>$null
    Write-Host "Grafana LGTM stopped." -ForegroundColor Green
}

switch ($Action) {
    "up"     { Invoke-Up }
    "down"   { Invoke-Down }
    "status" { docker compose -f $ComposeFile --profile telemetry ps }
}