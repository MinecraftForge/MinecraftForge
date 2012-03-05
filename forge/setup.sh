#!/bin/bash

echo "MinecraftForge Linux Setup Program"
echo 

SETUP_SCRIPT=$(readlink -e $0)
FORGEPATH=${SETUP_SCRIPT%/*}
MCPPATH=${FORGEPATH%/*}

if [ ! -x ${MCPPATH}/cleanup.sh ]; then
  echo "The path ${MCPPATH} does not appear to be a correctly setup MCP directory."
  exit 1
fi

echo ${FORGEPATH} ${MCPPATH}

if [ "$1" = "-skipdecompile" ]; then
  pushd ${MCPPATH} >/dev/null
  ${MCPPATH}/updatenames.sh -f
	popd >/dev/null
else
  if [ ! -f ${MCPPATH}/runtime/bin/fernflower.jar ]; then
      python ${FORGEPATH}/download_fernflower.py
  fi
  if [ ! -f ${MCPPATH}/runtime/bin/fernflower.jar ]; then
    echo "Failed to download fernflower, install it manually and re-run setup."
    exit 1
  fi
  rm -rf ${MCPPATH}/src
  
  pushd ${MCPPATH} > /dev/null
	${MCPPATH}/decompile.sh
	popd >/dev/null
fi  

rm -rf ${MCPPATH}/src_base ${MCPPATH}/src_work

find ${MCPPATH}/src -name '*.java' -execdir sed -i 's/\r//g' {} +
cp ${FORGEPATH}/MLProp.java ${MCPPATH}/src/minecraft/net/minecraft/src/MLProp.java
cp ${FORGEPATH}/MLProp.java ${MCPPATH}/src/minecraft_server/net/minecraft/src/MLProp.java

pushd ${MCPPATH} > /dev/null
${MCPPATH}/updatemd5.sh -f
popd > /dev/null

cp -a ${MCPPATH}/src ${MCPPATH}/src_base
cp -a ${MCPPATH}/src ${MCPPATH}/src_work

pushd ${MCPPATH}/src_work > /dev/null
	for i in $(find ${FORGEPATH}/patches/ -type f -name '*.patch'); do
    patch -p2 -i ${i}
	done
popd > /dev/null
