@ECHO OFF

REM Some versions of Java lack features necessary for Forge to function.
REM jvmchecker.jar will be invoked to check for these versions.
REM To skip this check, simply comment out the following line:
java -jar jvmchecker.jar 16 17 || EXIT 10

REM Forge requires a configured set of both JVM and program arguments.
REM Add custom JVM arguments to the user_jvm_args.txt
REM Add custom program arguments {such as nogui} to this file in the next line before the %* or
REM pass them to this script directly (i.e. 'run.bat nogui')
java @user_jvm_args.txt @libraries/@MAVEN_PATH@/win_args.txt %*
pause
