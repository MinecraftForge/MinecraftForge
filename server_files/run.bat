REM Forge requires a configured set of both JVM and program arguments.
REM Add custom JVM arguments to the user_jvm_args.txt
REM Add custom program arguments {such as nogui} to this file at the end of the next line
java @user_jvm_args.txt @libraries/@MAVEN_PATH@/win_args.txt %*
pause