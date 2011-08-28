cmd /C build.bat

cd ..

cmd /C reobfuscate.bat

set PATH=E:\cygwin\bin;%PATH%

cd forge

sh package.sh 1.0.6-beta2

pause
