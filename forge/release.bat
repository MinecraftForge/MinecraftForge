@echo off

call build.bat
if %ERRORLEVEL% NEQ 0 exit /b %ERRORLEVEL%

echo =================================== Release Start =================================

cd ..
runtime\bin\python\python_mcp runtime\reobfuscate.py
cd forge

set PATH=E:\cygwin\bin;%PATH%
sh package.sh

echo =================================== Release Finished %ERRORLEVEL% =================================
if %ERRORLEVEL% NEQ 0 exit /b %ERRORLEVEL%