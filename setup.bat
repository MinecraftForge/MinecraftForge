cd ..

rmdir /S /Q src

IF EXIST src_base (
  move src_base src
  rmdir /S /Q src_work
) ELSE (
  cmd /C decompile.bat
  cd src
  ..\runtime\bin\python\python_mcp ..\forge\lfcr.py ..\forge\modLoaderMP.patch ..\forge\modLoaderMP-win.patch
  ..\runtime\bin\applydiff.exe -uf -p2 < ..\forge\modLoaderMP-win.patch
  cd ..
  cmd /C recompile.bat
  cmd /C updatemd5.bat
)

mkdir src_base
mkdir src_work
xcopy  /Y /E src\* src_base
xcopy  /Y /E src\* src_work
cd src_work
@setlocal enabledelayedexpansion
@echo off

set PATH=..\forge\bin;%PATH%
echo Applying patches
for /f %%i in ('find ../forge/patches -type f') do (
  ..\runtime\bin\python\python_mcp ..\forge\lfcr.py %%i %%i
  ..\runtime\bin\applydiff.exe -uf -p2 < %%i
)

cd ../forge
pause

