echo off

echo MinecraftForge Windows Setup Program
echo:

@set PATH=%PATH%;%SystemDir%\system32;%SystemRoot%\System32

pushd .. >nul

xcopy /Y /E /I forge\conf\* conf

runtime\bin\python\python_mcp runtime\cleanup.py
runtime\bin\python\python_mcp runtime\decompile.py --jad

pushd src >nul

    if exist ..\jars\bin\minecraft.jar (
        del minecraft\net\minecraft\src\MLProp.java
        copy ..\forge\MLProp.java minecraft\net\minecraft\src\MLProp.java
        
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
        del minecraft_server\net\minecraft\src\MLProp.java
        copy ..\forge\MLProp.java minecraft_server\net\minecraft\src\MLProp.java

        ..\runtime\bin\python\python_mcp ..\forge\lfcr.py ../forge/modLoaderMP.patch ../forge/modLoaderMP.patch
        ..\runtime\bin\applydiff.exe -uf -p2 -i ../forge/modLoaderMP.patch
        
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


rem Removed until MCP's Update Names is fixed
rem cmd /C updatemcp.bat
rem cmd /C updatenames.bat
runtime\bin\python\python_mcp runtime\updatemd5.py
pause