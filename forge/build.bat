@cd ..

@rmdir /S /Q src
@mkdir src
xcopy  /Y /E src_work\* src\
xcopy /Y /E forge\forge_client\src\net\* src\minecraft\net
xcopy /Y /E forge\forge_common\net\* src\minecraft\net
xcopy /Y /E forge\forge_common\net\* src\minecraft_server\net

@cd forge
@cmd /C inject_version.bat ../src/minecraft/net/minecraft/src/forge/ForgeHooks.java
@cmd /C inject_version.bat ../src/minecraft_server/net/minecraft/src/forge/ForgeHooks.java
@cd ..

@echo  | call recompile.bat
