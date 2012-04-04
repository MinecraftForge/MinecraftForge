echo off

echo MinecraftForge Windows Setup Program
echo:

@set PATH=%PATH%;%SystemDir%\system32;%SystemRoot%\System32

if not exist "..\runtime\bin\fernflower.jar" (
    ..\runtime\bin\python\python_mcp download_fernflower.py
)
if not exist "..\runtime\bin\fernflower.jar" (
    echo Failed to download fernflower, install it manually and re-run setup.
    exit 1
)
    
pushd .. >nul

xcopy /Y /E /I forge\conf\* conf

if exist ".\src" (
    runtime\bin\python\python_mcp runtime\cleanup.py 
)
if exist ".\src" (
    echo  Please make sure to backup your modified files, and say yes when it asks you to do cleanup.
    exit 1
)
runtime\bin\python\python_mcp runtime\decompile.py

pushd src >nul

    if exist ..\jars\bin\minecraft.jar (
        for /f "delims=" %%a in ('dir /a -d /b /S ..\forge\patches\minecraft') do (
            pushd "%%a" 2>nul
            if errorlevel 1 (
                ..\runtime\bin\python\python_mcp ..\forge\lfcr.py "%%a" "%%a"
                ..\runtime\bin\applydiff.exe -uf -p2 -i "%%a"
            ) else popd
        )
        xcopy /Y /E ..\forge\src\minecraft\* minecraft
    )
    
    if exist ..\jars\minecraft_server.jar (
        for /f "delims=" %%a in ('dir /a -d /b /S ..\forge\patches\minecraft_server') do (
            pushd "%%a" 2>nul
            if errorlevel 1 (
                ..\runtime\bin\python\python_mcp ..\forge\lfcr.py "%%a" "%%a"
                ..\runtime\bin\applydiff.exe -uf -p2 -i "%%a"
            ) else popd
        )
        xcopy /Y /E ..\forge\src\minecraft_server\* minecraft_server    
    )
popd >nul

runtime\bin\python\python_mcp runtime\updatemcp.py -f
runtime\bin\python\python_mcp runtime\updatenames.py -f
runtime\bin\python\python_mcp runtime\updatemd5.py -f
pause