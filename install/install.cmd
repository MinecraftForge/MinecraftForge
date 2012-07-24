echo off
@set PATH=%PATH%;%SystemDir%\system32;%SystemRoot%\System32;..\runtime\bin\python
python_mcp install.py
pause
