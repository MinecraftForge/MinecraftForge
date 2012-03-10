#!/bin/bash

## note this should work with just dash or another basic shell, however the python/2 function trick requires bash :(

SETUP_SCRIPT=$(readlink -e $0)
FORGEPATH="${SETUP_SCRIPT%/*}"
BASEPATH="${FORGEPATH%/*}"
MCPPATH="${BASEPATH}/MCP"
LIBS="${BASEPATH}/libs"

CLI_ML="$(find "${LIBS}"   -iname 'ModLoader[^Mm]*zip')"
CLI_MLMP="$(find "${LIBS}" -iname 'ModLoaderMP*[^r].zip')"
SRV_MLMP="$(find "${LIBS}" -iname 'ModLoaderMP*Server.zip')"

if [ ! -s "${CLI_ML}" ] || [ ! -s "${CLI_MLMP}" ] || [ ! -s "${SRV_MLMP}" ] ; then
  echo "ModLoader and ModLoaderMP missing form ${LIBS}"
  exit 1
fi

if [ ! -s "${LIBS}"/minecraft_server.jar ] ; then
  echo "Please copy a CLEAN minecraft_server.jar to the ${LIBS} folder."
  exit 1
fi
if [ ! -d "${LIBS}"/bin/ ] || [ ! -d "${LIBS}"/resources/ ] ; then
  echo "Please copy a CLEAN .minecraft/ bin and resources folders to the ${LIBS} folder."
  exit 1
fi

rm -rf "${MCPPATH}"/jars/*
cp -a "${LIBS}"/minecraft_server.jar "${LIBS}"/bin/ "${LIBS}"/resources/ "${MCPPATH}"/jars/
mkdir tmp
cd tmp
jar -xf "${CLI_ML}"
jar -xf "${CLI_MLMP}"
jar -uf "${MCPPATH}/jars/bin/minecraft.jar" ./
rm -rf ../tmp/*
jar -xf "${SRV_MLMP}"
jar -uf "${MCPPATH}/jars/minecraft_server.jar" ./
cd ..
rm -rf tmp

rm -rf "${MCPPATH}"/src
