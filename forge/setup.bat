set CYWIN=E:\cygwin\bin

cd ..

rmdir /S /Q src

IF EXIST src_base (
move src_base src;
rmdir /S /Q src_work
) ELSE (
cmd /C decompile.bat
cd src
%CYGWIN%\patch -u -p2 < ..\forge\modLoaderMP.patch
cd ..
cmd /C recompile.bat
cmd /C updatemd5.bat
)

mkdir src_base
mkdir src_work
xcopy  /Y /E src\* src_base
xcopy  /Y /E src\* src_work
cd src_work
%CYGWIN%\patch -u -p2 < ..\forge\minecraft.patch
