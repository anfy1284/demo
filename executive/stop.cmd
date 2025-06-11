@echo off
echo Stopping the project...

:: Kill all java processes (or only specific one if needed)
taskkill /IM java.exe /F

echo Java process has been terminated.

exit /b