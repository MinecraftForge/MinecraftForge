set HOME=%CD%
set MCP=%HOME%\..
xcopy  /Y /E src\* ..\src

cd %MCP%\src
%MCP%\runtime\bin\python\python_mcp %HOME%\lfcr.py %HOME%\minecraft.patch %HOME%\minecraft-win.patch
%MCP%\runtime\bin\applydiff.exe -uf -p2 < %HOME%\minecraft-win.patch
pause
