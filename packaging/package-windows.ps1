param(
    [switch]$SkipBuild
)

$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

[xml]$pom = Get-Content -Raw "pom.xml"
$projectVersion = [string]$pom.project.version
$numericVersion = ($projectVersion -split '-')[0]
$jarName = "sonic-candle-$projectVersion.jar"
$jarPath = Join-Path $projectRoot "target\$jarName"

if (-not $SkipBuild) {
    if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
        throw "Maven was not found in PATH. Build the project from NetBeans first or install Maven."
    }
    & mvn --batch-mode --no-transfer-progress clean package
    if ($LASTEXITCODE -ne 0) {
        throw "Maven could not build Sonic Candle."
    }
}

if (-not (Test-Path $jarPath)) {
    throw "Executable JAR not found: $jarPath. Run Clean and Build in NetBeans first."
}
if (-not (Get-Command jpackage -ErrorAction SilentlyContinue)) {
    throw "jpackage was not found. Select a full JDK 17 or newer instead of a JRE."
}

$wixCandidates = @(
    "${env:ProgramFiles(x86)}\WiX Toolset v3.14\bin",
    "${env:ProgramFiles(x86)}\WiX Toolset v3.11\bin"
)
if (-not (Get-Command candle.exe -ErrorAction SilentlyContinue)) {
    foreach ($candidate in $wixCandidates) {
        if (Test-Path (Join-Path $candidate "candle.exe")) {
            $env:PATH = "$candidate;$env:PATH"
            break
        }
    }
}
if (-not (Get-Command candle.exe -ErrorAction SilentlyContinue)) {
    throw "WiX Toolset 3 was not found. Install WiX 3.14 and run this script again."
}

$inputDirectory = Join-Path $projectRoot "build\jpackage-input"
$outputDirectory = Join-Path $projectRoot "dist\windows"
if (Test-Path $inputDirectory) {
    Remove-Item -Recurse -Force $inputDirectory
}
if (Test-Path $outputDirectory) {
    Remove-Item -Recurse -Force $outputDirectory
}
New-Item -ItemType Directory -Force $inputDirectory | Out-Null
New-Item -ItemType Directory -Force $outputDirectory | Out-Null
Copy-Item $jarPath (Join-Path $inputDirectory $jarName)

$bundledFfmpeg = $false
$ffmpeg = Join-Path $projectRoot "tools\ffmpeg.exe"
$ffprobe = Join-Path $projectRoot "tools\ffprobe.exe"
if ((Test-Path $ffmpeg) -and (Test-Path $ffprobe)) {
    $packagedTools = Join-Path $inputDirectory "tools"
    New-Item -ItemType Directory -Force $packagedTools | Out-Null
    Copy-Item $ffmpeg $packagedTools
    Copy-Item $ffprobe $packagedTools
    $bundledFfmpeg = $true
}

$jpackageArguments = @(
    "--type", "exe",
    "--name", "Sonic Candle",
    "--input", $inputDirectory,
    "--main-jar", $jarName,
    "--main-class", "com.soniccandle.App",
    "--app-version", $numericVersion,
    "--vendor", "JavCif & Candle",
    "--description", "Approachable music visualizer generator",
    "--copyright", "Copyright 2026 JavCif & Candle",
    "--about-url", "https://github.com/JAVCIF/sonic-candle-2",
    "--license-file", (Join-Path $projectRoot "LICENSE"),
    "--icon", (Join-Path $projectRoot "packaging\sonic-candle.ico"),
    "--dest", $outputDirectory,
    "--win-dir-chooser",
    "--win-menu",
    "--win-menu-group", "Sonic Candle",
    "--win-shortcut",
    "--win-per-user-install",
    "--win-upgrade-uuid", "7340A074-492B-4ED1-BCED-F1B39D00DE0E"
)

& jpackage @jpackageArguments
if ($LASTEXITCODE -ne 0) {
    throw "jpackage could not create the Windows installer."
}

$generatedInstaller = Get-ChildItem $outputDirectory -Filter "*.exe" |
    Select-Object -First 1
if (-not $generatedInstaller) {
    throw "jpackage finished without producing an EXE installer."
}
$finalInstaller = Join-Path $outputDirectory (
    "sonic-candle-$projectVersion-windows-x64.exe")
if ($generatedInstaller.FullName -ne $finalInstaller) {
    Move-Item -Force $generatedInstaller.FullName $finalInstaller
}

$jarOutputDirectory = Join-Path $projectRoot "dist"
New-Item -ItemType Directory -Force $jarOutputDirectory | Out-Null
$finalJar = Join-Path $jarOutputDirectory $jarName
Copy-Item -Force $jarPath $finalJar

Write-Host ""
Write-Host "Build completed:" -ForegroundColor Green
Write-Host "  JAR: $finalJar"
Write-Host "  EXE: $finalInstaller"
if ($bundledFfmpeg) {
    Write-Host "  FFmpeg and FFprobe were bundled from tools\." -ForegroundColor Green
} else {
    Write-Warning "FFmpeg was not bundled. Install it in PATH or rebuild after placing ffmpeg.exe and ffprobe.exe in tools\."
}
