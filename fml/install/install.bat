echo off
@set PATH=%PATH%;%SystemDir%\system32;%SystemRoot%\System32;.\python\
python_fml install.py %*
pause
