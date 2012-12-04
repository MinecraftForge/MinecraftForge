@echo off

@SET PATH=%PATH%;.\GnuWin32\

if exist "fml" rmdir /S /Q fml

for %%f in (fml-*.zip) do (
    if exist "fml" (
        echo Multiple FML zips detected, aborting: %%~nf
        exit /b 1
    )
    echo Extracting %%~nf
    unzip -q %%~nf.zip
)

if not exist "fml" (
    echo Could not find a valid FML FML jar, aborting
    exit /b 1
)

fml\python\python_fml setup.py --no-extract %*