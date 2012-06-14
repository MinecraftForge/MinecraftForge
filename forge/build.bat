@echo off
echo =================================== Build Start =================================
cd ..

rmdir /S /Q src
mkdir src

echo src_work -^> src
xcopy /Q /Y /E src_work\* src\
echo.

echo forge_client -^> minecraft
xcopy /Y /E forge\forge_client\src\net\* src\minecraft\net
echo.

echo forge_server -^> minecraft_server
xcopy /Y /E forge\forge_server\src\net\* src\minecraft_server\net
echo.

echo forge_common -^> minecraft
xcopy /Y /E forge\forge_common\net\* src\minecraft\net
echo.

echo forge_common -^> minecraft_server
xcopy /Y /E forge\forge_common\net\* src\minecraft_server\net
echo.

cd forge
cmd /C inject_version.bat ../src/minecraft/net/minecraft/src/forge/ForgeHooks.java
cmd /C inject_version.bat ../src/minecraft_server/net/minecraft/src/forge/ForgeHooks.java
cd ..

runtime\bin\python\python_mcp runtime\recompile.py

set ret=%ERRORLEVEL%
cd forge
echo =================================== Build Finished %ret% =================================
if %ret% NEQ 0 exit /b %ret%
