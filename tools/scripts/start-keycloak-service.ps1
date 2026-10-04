<#
.SYNOPSIS
    Manages the local Keycloak identity provider container.
.DESCRIPTION
    Starts, stops, and monitors the Keycloak container with the pre-configured deepproject realm.
    Requires PostgreSQL to be running first — use start-dev-db.ps1.
.NOTES
    Admin console : http://localhost:8090 (admin / admin)
    Realm issuer  : http://localhost:8090/realms/deepproject
    Client ID     : deepproject-api (public client)
.PARAMETER Action
    up     : Start Keycloak and wait for readiness.
    down   : Stop Keycloak container.
    status : Show Keycloak container status.
#>
param (
    [ValidateSet("up", "down", "status")]
    [string]$Action = "up"
)

$ErrorActionPreference = "Stop"
$ComposeFile = (Join-Path $PSScriptRoot "docker-compose.dev.yaml")

function Wait-ForKeycloak {
    Write-Host "Waiting for Keycloak to be ready (may take 30-60s on first run)..." -ForegroundColor Cyan
    $attempts = 0
    while ($attempts -lt 60) {
        $status = docker inspect --format='{{json .State.Health.Status}}' deepproject-keycloak 2>$null
        if ($status -eq '"healthy"') {
            Write-Host "Keycloak is ready." -ForegroundColor Green
            Write-Host "  Admin console : http://localhost:8090/admin" -ForegroundColor DarkGray
            Write-Host "  Realm issuer  : http://localhost:8090/realms/deepproject" -ForegroundColor DarkGray
            Write-Host "  Clients       : deepproject-api (public)" -ForegroundColor DarkGray
            return
        }
        Start-Sleep -Seconds 2
        $attempts++
    }
    throw "Keycloak failed to report healthy within 120 seconds."
}

function Invoke-Up {
    Write-Host "`n=== Starting Keycloak ===" -ForegroundColor Cyan
    docker compose -f $ComposeFile up -d keycloak
    Wait-ForKeycloak
}

function Invoke-Down {
    Write-Host "Stopping Keycloak..." -ForegroundColor Yellow
    docker compose -f $ComposeFile stop keycloak 2>$null
    Write-Host "Keycloak stopped." -ForegroundColor Green
}

switch ($Action) {
    "up"     { Invoke-Up }
    "down"   { Invoke-Down }
    "status" { docker compose -f $ComposeFile ps keycloak }
}