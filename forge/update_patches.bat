@setlocal enabledelayedexpansion
@echo off

set PATH=.\bin;%PATH%

python update_patches.py

echo Grabbing copy of conf folder
for /f %%i in ('find conf -type f') do (
  set file=%%i
  if %%i NEQ ".gitignore" (
    del !file:/=\!
  )
)

xcopy ..\conf conf /E /I /Y
tr -d \r < ../conf/mcp.cfg > conf/mcp.cfg
tr -d \r < ../conf/patches/Start.java > conf/patches/Start.java

echo Finished
pause
