#!/usr/bin/env sh

# Some versions of Java lack features necessary for Forge to function.
# CheckVersion.java will be invoked to check for these versions.
# To skip this check, simply comment out the following line:
if ! java CheckVersion.java ; then exit 0; fi

# Forge requires a configured set of both JVM and program arguments.
# Add custom JVM arguments to the user_jvm_args.txt
# Add custom program arguments {such as nogui} to this file in the next line before the "$@" or
# pass them to this script directly (i.e. 'run.sh nogui')
java @user_jvm_args.txt @libraries/@MAVEN_PATH@/unix_args.txt "$@"