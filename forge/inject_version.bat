@echo off
setlocal enableextensions enabledelayedexpansion
FOR /F "tokens=1-4" %%A IN (version.txt) DO (
    set major=%%A
    set minor=%%B
    set revision=%%C
    set build=%%D
)
set file=%*

echo Injecting Revision !major!.!minor!.!revision!.!build! %file%

set PATH=.\bin;%PATH%
sed s/majorVersion=0/majorVersion=!major!/ <!file! | ^
sed s/minorVersion=0/minorVersion=!minor!/ | ^
sed s/revisionVersion=0/revisionVersion=!revision!/ | ^
sed s/buildVersion=0/buildVersion=!build!/ | ^
tr -d '\r' >!file!.tmp

move /Y !file:/=\!.tmp !file:/=\! >nul
