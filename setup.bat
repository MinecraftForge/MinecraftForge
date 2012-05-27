@echo off
echo =================================== Setup Start =================================
pushd .. >nul

  if "%1" NEQ "-skipdecompile" (
    if not exist "runtime\bin\fernflower.jar" (
      popd >nul
        ..\runtime\bin\python\python_mcp download_fernflower.py
      pushd .. >nul
    )
    if not exist "runtime\bin\fernflower.jar" (
      echo Failed to download fernflower, install it manually and re-run setup.
      exit 1
    )
    rmdir /S /Q src
    echo | cmd /C decompile.bat -d -n -r
  )

  echo | cmd /C updatemd5.bat -f

  rmdir /S /Q src-base
  rmdir /S /Q src-work
  mkdir src-base
  mkdir src-work

  xcopy /Y /E /Q src\* src-base
  xcopy /Y /E /Q src\* src-work
popd >nul
..\runtime\bin\python\python_mcp cleanup_source.py ..\src-base ..\src-work
..\runtime\bin\python\python_mcp applypatches.py patches ..\src-work ..
echo =================================== Setup Finished =================================

if NOT "%1"=="-skipdecompile" (
  pause
)