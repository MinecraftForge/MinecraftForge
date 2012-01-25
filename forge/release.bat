@cmd /C build.bat

@cd ..

@echo | cmd /C reobfuscate.bat

@set PATH=E:\cygwin\bin;%PATH%

@cd forge

@sh package.sh

pause
