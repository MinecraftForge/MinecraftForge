@echo off
..\runtime\bin\python\python_mcp setup.py %*
if NOT "%1"=="-skipdecompile" (
  pause
)