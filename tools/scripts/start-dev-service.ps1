<#
.SYNOPSIS
    Centralized orchestrator for all local development services.
.DESCRIPTION
    Starts PostgreSQL + Flyway + Keycloak + optionally Grafana LGTM (OTel).
    Delegates to start-dev-db.ps1, start-keycloak-service.ps1, and start-otel-service.ps1.
.PARAMETER Action
    up     : Start all infrastructure (database + Keycloak) and optionally telemetry.
    down   : Stop all local dev containers.
    reset  : Destroy all volumes and recreate everything from scratch.
    status : Show status of all containers.
.PARAMETER WithTelemetry
    Also launches Grafana LGTM (OTel tracing, metrics, logs).
.PARAMETER SkipTestData
    Forwarded to start-dev-db.ps1 to skip local test-data seeding.
#>
param (
    [ValidateSet("up", "down", "reset", "status")]
    [string]$Action = "up",

    [switch]$WithTelemetry,
    [switch]$SkipTestData
)

$ErrorActionPreference = "Stop"
$ScriptDir = $PSScriptRoot
$DbScript = Join-Path $ScriptDir "start-dev-db.ps1"
$KeycloakScript = Join-Path $ScriptDir "start-keycloak-service.ps1"
$OtelScript = Join-Path $ScriptDir "start-otel-service.ps1"

function Invoke-Db {
    if ($SkipTestData) {
        & $DbScript -Action $args[0] -SkipTestData
    } else {
        & $DbScript -Action $args[0]
    }
}

function Invoke-Up {
    Invoke-Db "up"
    & $KeycloakScript -Action up
    if ($WithTelemetry) {
        & $OtelScript -Action up
    }
    Write-Host "`n=== All services ready ===" -ForegroundColor Green
    Write-Host "  App         : http://localhost:8080" -ForegroundColor DarkGray
    Write-Host "  Keycloak    : http://localhost:8090" -ForegroundColor DarkGray
    Write-Host "  PostgreSQL  : localhost:5432" -ForegroundColor DarkGray
    if ($WithTelemetry) {
        Write-Host "  Grafana     : http://localhost:3000" -ForegroundColor DarkGray
    }
}

function Invoke-Down {
    Write-Host "`nStopping all services..." -ForegroundColor Yellow
    Invoke-Db "down"
    & $KeycloakScript -Action down
    & $OtelScript -Action down
    Write-Host "All services stopped." -ForegroundColor Green
}

function Invoke-Reset {
    Write-Host "`nResetting all services (destroying volumes)..." -ForegroundColor Red
    Invoke-Db "reset"
    & $KeycloakScript -Action down
    & $OtelScript -Action down
    & $KeycloakScript -Action up
    if ($WithTelemetry) {
        & $OtelScript -Action up
    }
    Write-Host "`n=== All services reset and ready ===" -ForegroundColor Green
}

switch ($Action) {
    "up"     { Invoke-Up }
    "down"   { Invoke-Down }
    "reset"  { Invoke-Reset }
    "status" {
        Invoke-Db "status"
        & $KeycloakScript -Action status
        & $OtelScript -Action status
    }
}