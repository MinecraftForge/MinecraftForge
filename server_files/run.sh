#!/usr/bin/env sh
# Add custom JVM arguments (such as RAM allocation) to the user_jvm_args.txt

java -jar @SHIM_JAR_FILE@ --onlyCheckJava || exit 1

# Add custom program arguments (such as nogui) to the next line before the "$@" or pass them to this script directly
java @user_jvm_args.txt @libraries/net/minecraftforge/forge/1.20.2-pre3-43.1.329-1.20.2-dev/unix_args.txt "$@"
