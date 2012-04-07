@echo off
if "%1"=="" (
    echo You must enter a build number to promote to the latest
    exit /b 1
)

echo Jenkins build script started for build #%1
rmdir /S /Q ..\conf
xcopy /Y /E /I conf\* ..\conf

..\runtime\bin\python\python_mcp setup.py -skipdecompile
if %ERRORLEVEL% NEQ 0 exit /b %ERRORLEVEL%

..\runtime\bin\python\python_mcp release.py %1
if %ERRORLEVEL% NEQ 0 exit /b %ERRORLEVEL%

cd forge-*%1
if %ERRORLEVEL% NEQ 0 (
    echo Release failed to produce artifacts %1
    exit /b 1
)

for /f "delims=\" %%a in ("%cd%") do SET VERSION=%%~nxa
SET VERSION=%VERSION:~6%
echo Forge Version: %VERSION%

E:\MediafireUploader.exe Forge-%VERSION% minecraftforge-client-%VERSION%.zip minecraftforge-server-%VERSION%.zip minecraftforge-src-%VERSION%.zip

