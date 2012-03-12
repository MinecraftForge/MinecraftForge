@cmd /C build.bat

if %ERRORLEVEL% NEQ 0 exit 1

@cd ..

@echo | cmd /C reobfuscate.bat

@set PATH=E:\cygwin\bin;%PATH%

@cd forge

@sh package.sh

pause
