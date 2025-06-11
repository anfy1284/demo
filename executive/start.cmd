@echo off
echo Starting the project in background...

set JAR_PATH=demo-0.0.1-SNAPSHOT.jar
set PORT=8080

if not exist %JAR_PATH% (
    echo Error: %JAR_PATH% not found!
    pause
    exit /b
)

:: Start the jar using PowerShell in hidden mode
powershell -WindowStyle Hidden -Command "Start-Process 'java' -ArgumentList '-jar', '%JAR_PATH%' -WindowStyle Hidden"

echo Waiting for the server to start on http://localhost:%PORT% ...

:: Wait until the server is ready
:wait_loop
timeout /t 2 /nobreak >nul
(
    echo >nul 2>nul >\\localhost\pipe\test
) || (
    powershell -Command "(New-Object System.Net.Sockets.TcpClient('localhost', %PORT%)).Dispose()" 2>nul
    if %errorlevel%==0 (
        goto server_ready
    ) else (
        goto wait_loop
    )
)

:server_ready
echo Server is up!

:: Open the site in the browser
start http://localhost:%PORT%

exit /b