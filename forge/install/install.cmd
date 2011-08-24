@echo off
echo Minecraft Forge Install and Build Script v1.2
echo -Searching for components...
set MCP=ERROR!
set HOME=ERROR!
if /i exist "%~dp0\%~nx0" (set MCP="%~dp0.."&& set HOME="%~dp0")
if /i exist "%~dp0..\install\%~nx0" (set MCP="%~dp0..\.."&& set HOME="%~dp0..")
if /i exist "%~dp0\forge\%~nx0" (set MCP="%~dp0" && set HOME="%~dp0\forge")
if %MCP%==ERROR! (echo -Error: Forge Not Detected! && echo -Please be sure you've installed it properly.)
if %HOME%==ERROR! (echo -Error: MCP Not Detected! && echo -Please be sure you've installed it - and Forge - properly.)
echo -MCP detected in %MCP%
echo -Forge detected in %HOME%
echo -Rectifying Paths...
set MCP=%MCP:"=%
set MCP=%MCP:'=%
set HOME=%HOME:"=%
set HOME=%HOME:'=%
if /i not exist "%MCP%\src\minecraft\net\minecraft\client\minecraft.java" (echo -Decompiled source not detected, decompiling minecraft.jar... && pushd %MCP% && echo -NEW SESSION >> compile.log && cmd /c "runtime\bin\python\python_mcp runtime\decompile.py %*" && popd) else (echo -Decompiled source detected)
pushd %MCP%\src
echo -Applying forge to the sourcetree...
xcopy /Y /E /G /Q "%HOME%"\forge_client\src\net\* "%MCP%"\src\minecraft\net
xcopy /Y /E /G /Q "%HOME%"\forge_common\net\* "%MCP%"\src\minecraft\net
echo D | xcopy /Y /E /G /Q "%HOME%\forge_common\net\*" "%MCP%"\src\minecraft_server\net
echo -NEW SESSION >> forgepatch.log
"%MCP%\runtime\bin\python\python_mcp" "%HOME%\lfcr.py" "%HOME%\minecraft.patch" "%HOME%\minecraft-win.patch" >> forgepatch.log
echo -Patching necessary source files for forge... 
"%MCP%\runtime\bin\applydiff.exe" -uf -p2 < "%HOME%\minecraft-win.patch" >> forgepatch.log
popd
echo -Forge ready for use, recompiling...
pushd %MCP%
cmd /c "runtime\bin\python\python_mcp runtime\recompile.py %*"
popd
echo -Recompilation complete!
echo.
