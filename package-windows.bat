@echo off
setlocal
cd /d "%~dp0"
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "packaging\package-windows.ps1" %*
if errorlevel 1 (
    echo.
    echo Windows packaging failed. Review the message above.
    exit /b 1
)
echo.
echo Sonic Candle JAR and EXE are ready inside dist\.
endlocal
