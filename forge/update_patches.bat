@setlocal enabledelayedexpansion
@echo off

set PATH=.\bin;%PATH%

echo Creating directories
for /f %%i in ('find ../src_work -type d') do (
  set file=%%i
  set file=!file:~11!
  rem echo patches!file!
  "./bin/mkdir.exe" --parents "patches!file!"
)
echo Creating patches
for /f %%i in ('find ../src_work -type f') do (
  set file=%%i
  set file=!file:~11!
  rem echo !file!
  diff -u ../src_base!file! ../src_work!file! -r --strip-trailing-cr --new-file | sed -e "1,2s/[0-9-]* [0-9:\.]* [+-][0-9]*\b/0000-00-00 00:00:00.000000000 -0000/" | tr -d '\r'  > patches!file!.patch
)

echo Removing empty patches
find patches -size 0 -type f -regex .+\.patch -delete
for /f %%i in ('find patches -depth -empty -type d') do (
  set file=%%i
  if "!file:.svn=!" EQU "!file!" (
    rmdir "%%i"
  )
)

echo Grabbing copy of conf folder
for /f %%i in ('find conf -type f') do (
  set file=%%i
  if "!file:.svn=!" EQU "!file!" (
    del !file:/=\!
  )
)

for /f %%i in ('find conf -depth -empty -type d') do (
  set file=%%i
  if "!file:.svn=!" EQU "!file!" (
    rmdir "%%i"
  )
)

xcopy ..\conf conf /E /I /Y

echo Finished
pause
