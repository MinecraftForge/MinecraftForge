cd ..

rmdir /S /Q src
mkdir src
xcopy  /Y /E src_work\* src\

xcopy /Y /E forge\forge_common\net\* src\minecraft\net
xcopy /Y /E forge\forge_common\net\* src\minecraft_server\net

cmd /C recompile.bat
