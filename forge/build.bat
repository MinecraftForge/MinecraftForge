cd ..

rmdir /S /Q src
mkdir src
xcopy  /Y /E src_work\* src\

cmd /C recompile.bat
