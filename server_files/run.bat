@echo off
REM Add custom JVM arguments (such as RAM allocation) to the user_jvm_args.txt

java -jar @SHIM_JAR_FILE@ --onlyCheckJava
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Java check failed.
    echo If you're struggling to get Java to work, ask for help on the forums or Discord mentioned in the readme.
    goto :exit
)

REM Add custom program arguments (such as nogui) to the next line before the %* or pass them to this script directly
java @libraries/@MAVEN_PATH@/win_args.txt %*

:exit
pause
