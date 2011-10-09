echo off

echo MinecraftForge Windows Setup Program
echo:

pushd .. >nul

cmd /C cleanup.bat
cmd /C decompile.bat

pushd src >nul
    ..\runtime\bin\python\python_mcp ..\forge\lfcr.py ../forge/modLoaderMP.patch ../forge/modLoaderMP.patch
    ..\runtime\bin\applydiff.exe -uf -p2 -i ../forge/modLoaderMP.patch
    ..\runtime\bin\python\python_mcp ..\forge\lfcr.py ../forge/mlprop.patch ../forge/mlprop.patch
    ..\runtime\bin\applydiff.exe -uf -p1 -i ../forge/mlprop.patch

    for /f "delims=" %%a in ('dir /a -d /b /S ..\forge\patches') do (
        pushd "%%a" 2>nul
        if errorlevel 1 (
            ..\runtime\bin\python\python_mcp ..\forge\lfcr.py "%%a" "%%a"
            ..\runtime\bin\applydiff.exe -uf -p2 -i "%%a"
        ) else popd
    )
popd >nul

xcopy /Y /E forge\src\* src

cmd /C updatemd5.bat