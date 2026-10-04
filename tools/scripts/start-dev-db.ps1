<#
.SYNOPSIS
    Manages the local PostgreSQL development database, Flyway migrations, and test-data seeding.
.DESCRIPTION
    Spins up PostgreSQL, runs Flyway schema migrations, and loads local test seed data from /test-data.
    Use start-dev-service.ps1 for the full stack including Keycloak and OTel.
.PARAMETER Action
    up       : Start PostgreSQL, run Flyway migrate, seed test-data.
    down     : Stop PostgreSQL and remove Flyway container.
    migrate  : Run Flyway migrate against running database.
    info     : Run Flyway info to display migration status.
    repair   : Run Flyway repair to repair schema history.
    seed     : Apply test-data SQL scripts against running database.
    reset    : Destroy all volumes and recreate database from scratch.
    status   : Show PostgreSQL and Flyway container status.
.PARAMETER SkipTestData
    If specified, skips applying scripts from the /test-data folder.
#>
param (
    [ValidateSet("up", "down", "migrate", "info", "repair", "seed", "reset", "status")]
    [string]$Action = "up",

    [switch]$SkipTestData
)

$ErrorActionPreference = "Stop"
$ToolsDir = $PSScriptRoot
$ProjectRoot = (Resolve-Path "$ToolsDir\..\..").Path
$ComposeFile = "$ToolsDir\docker-compose.dev.yaml"
$DbUser = "postgres"
$DbName = "deepproject_db"

function Invoke-Flyway {
    param ([string]$FlywayCommand = "migrate")

    Write-Host "Spinning up Flyway container to run '$FlywayCommand'..." -ForegroundColor Cyan

    docker compose -f $ComposeFile --profile tools run --rm `
        -e FLYWAY_URL="jdbc:postgresql://postgres:5432/$DbName" `
        -e FLYWAY_USER="$DbUser" `
        -e FLYWAY_PASSWORD="postgres" `
        -e FLYWAY_BASELINE_ON_MIGRATE="true" `
        -e FLYWAY_CONNECT_RETRIES="10" `
        -e FLYWAY_INIT_SQL="SET TIME ZONE 'UTC'" `
        flyway $FlywayCommand

    if ($LASTEXITCODE -eq 0) {
        Write-Host "Flyway $FlywayCommand completed successfully." -ForegroundColor Green
    } else {
        throw "Flyway $FlywayCommand failed with exit code $LASTEXITCODE."
    }
}

function Wait-ForPostgres {
    Write-Host "Waiting for PostgreSQL to be healthy..." -ForegroundColor Cyan
    $attempts = 0
    while ($attempts -lt 30) {
        $status = docker inspect --format='{{json .State.Health.Status}}' deepproject-postgres 2>$null
        if ($status -eq '"healthy"') {
            Write-Host "PostgreSQL is healthy ($DbName on port 5432)." -ForegroundColor Green
            return
        }
        Start-Sleep -Seconds 1
        $attempts++
    }
    throw "PostgreSQL failed to report healthy within 30 seconds."
}

function Invoke-Up {
    Write-Host "`n=== Starting PostgreSQL ===" -ForegroundColor Cyan
    docker compose -f $ComposeFile up -d postgres
    Wait-ForPostgres
    Invoke-Flyway "migrate"
    if (-not $SkipTestData) {
        Invoke-Seed
    } else {
        Write-Host "Skipping test-data seeding (-SkipTestData)." -ForegroundColor DarkGray
    }
    Write-Host "=== Database ready ===" -ForegroundColor Green
}

function Invoke-Down {
    Write-Host "Stopping PostgreSQL and removing Flyway container..." -ForegroundColor Yellow
    docker compose -f $ComposeFile --profile tools rm -sf flyway 2>$null
    docker compose -f $ComposeFile stop postgres 2>$null
    Write-Host "Database stopped." -ForegroundColor Green
}

function Invoke-Reset {
    Write-Host "Resetting database (destroying all volumes)..." -ForegroundColor Red
    docker compose -f $ComposeFile --profile tools down -v 2>$null
    Invoke-Up
}

function Invoke-Seed {
    Write-Host "Discovering and applying local /test-data scripts..." -ForegroundColor Cyan
    $testDataDir = "$ProjectRoot\database\scripts\test-data"

    if (-not (Test-Path $testDataDir)) {
        Write-Host "No /test-data folder found at $testDataDir." -ForegroundColor DarkGray
        return
    }

    $testDataFiles = Get-ChildItem -Path $testDataDir -Filter "*.sql" | Sort-Object Name
    if ($testDataFiles.Count -eq 0) {
        Write-Host "No test data scripts found in $testDataDir." -ForegroundColor DarkGray
        return
    }

    foreach ($file in $testDataFiles) {
        Write-Host "  -> Applying: $($file.Name)" -ForegroundColor Magenta
        Get-Content $file.FullName -Raw | docker exec -i deepproject-postgres psql -U $DbUser -d $DbName -v "ON_ERROR_STOP=1"
        if ($LASTEXITCODE -ne 0) {
            Write-Warning "Test data execution for $($file.Name) finished with code: $LASTEXITCODE"
        }
    }
    Write-Host "Test data seeded." -ForegroundColor Green
}

switch ($Action) {
    "up"      { Invoke-Up }
    "down"    { Invoke-Down }
    "migrate" { Invoke-Flyway "migrate" }
    "info"    { Invoke-Flyway "info" }
    "repair"  { Invoke-Flyway "repair" }
    "seed"    { Invoke-Seed }
    "reset"   { Invoke-Reset }
    "status"  { docker compose -f $ComposeFile --profile tools ps }
}