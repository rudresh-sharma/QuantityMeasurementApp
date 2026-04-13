$ErrorActionPreference = "Stop"

$rootDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$authSecrets = Join-Path $rootDir "authentication-service\secrets\oauth-secrets.properties"
$mavenRepoLocal = Join-Path $rootDir ".m2-repo"

function Resolve-MavenCommand {
    $mvnw = Join-Path $rootDir "mvnw.cmd"
    if (Test-Path $mvnw) {
        return $mvnw
    }

    if ($env:MAVEN_HOME) {
        $mavenHomeMvn = Join-Path $env:MAVEN_HOME "bin\mvn.cmd"
        if (Test-Path $mavenHomeMvn) {
            return $mavenHomeMvn
        }
    }

    if ($env:M2_HOME) {
        $m2HomeMvn = Join-Path $env:M2_HOME "bin\mvn.cmd"
        if (Test-Path $m2HomeMvn) {
            return $m2HomeMvn
        }
    }

    foreach ($candidate in @("mvn", "mvn.cmd", "mvn.bat")) {
        $cmd = Get-Command $candidate -ErrorAction SilentlyContinue
        if ($cmd) {
            return $candidate
        }
    }

    throw "Maven was not found. Add Maven to PATH, set MAVEN_HOME, or add mvnw.cmd to the repo root."
}

function New-CmdSetStatement {
    param(
        [Parameter(Mandatory = $true)][string]$Assignment
    )

    return 'set "{0}"' -f $Assignment
}

function Start-ServiceWindow {
    param(
        [Parameter(Mandatory = $true)][string]$Title,
        [Parameter(Mandatory = $true)][string]$Module,
        [string[]]$EnvVars = @()
    )

    $segments = @(
        ('title "{0}"' -f $Title),
        ('cd /d "{0}"' -f $rootDir)
    )

    foreach ($envVar in $EnvVars) {
        $segments += New-CmdSetStatement -Assignment $envVar
    }

    $segments += ('"{0}" -Dmaven.repo.local="{1}" -pl {2} spring-boot:run' -f $script:mavenCmd, $script:mavenRepoLocal, $Module)

    $cmdLine = $segments -join " && "
    Start-Process -FilePath "cmd.exe" -ArgumentList "/k", $cmdLine -WindowStyle Normal -WorkingDirectory $rootDir
}

try {
    $script:mavenCmd = Resolve-MavenCommand
    $script:mavenRepoLocal = $mavenRepoLocal

    if (-not (Test-Path $script:mavenRepoLocal)) {
        New-Item -ItemType Directory -Path $script:mavenRepoLocal | Out-Null
    }

    if (-not (Test-Path $authSecrets)) {
        Write-Warning "OAuth secrets file not found at: $authSecrets"
        Write-Warning "Auth service will still start, but Google OAuth login may fail."
    }

    Write-Host "Starting all backend services..."
    Write-Host ""
    Write-Host "Local profile enabled: SPRING_PROFILES_ACTIVE=local"
    Write-Host "User and Quantity services will use in-memory H2 (no MySQL required)."
    Write-Host ""

    Start-ServiceWindow -Title "Eureka Server :8761" -Module "eureka-server"
    Start-Sleep -Seconds 8

    Start-ServiceWindow -Title "User Service :8083" -Module "user-service" -EnvVars @(
        "SPRING_PROFILES_ACTIVE=local",
        "FRONTEND_ORIGIN=http://localhost:4200"
    )
    Start-ServiceWindow -Title "Auth Service :8081" -Module "authentication-service" -EnvVars @(
        "FRONTEND_ORIGIN=http://localhost:4200",
        "FRONTEND_GOOGLE_REDIRECT=http://localhost:4200/oauth-success",
        "GOOGLE_OAUTH_CALLBACK_URI=http://localhost:8081/login/oauth2/code/google"
    )
    Start-ServiceWindow -Title "Quantity Service :8082" -Module "quantity-measurement-service" -EnvVars @(
        "SPRING_PROFILES_ACTIVE=local",
        "FRONTEND_ORIGIN=http://localhost:4200"
    )
    Start-Sleep -Seconds 8

    Start-ServiceWindow -Title "API Gateway :8080" -Module "api-gateway"
    Start-ServiceWindow -Title "Admin Server :9090" -Module "admin-server"

    Write-Host ""
    Write-Host "Launch commands started in separate windows."
    Write-Host "Eureka:  http://localhost:8761"
    Write-Host "Gateway: http://localhost:8080"
    Write-Host "Admin:   http://localhost:9090"
}
catch {
    Write-Error $_.Exception.Message
    exit 1
}
