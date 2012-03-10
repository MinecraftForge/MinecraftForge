#!/bin/bash

echo "MinecraftForge Linux Setup Program"
echo 

SETUP_SCRIPT=$(readlink -e $0)
FORGEPATH="${SETUP_SCRIPT%/*}"
BASEPATH="${FORGEPATH%/*}/"
MCPPATH="${BASEPATH}MCP"
JARS="${BASEPATH}jars"


python () {
	if which python2 > /dev/null ; then
		python2 "$@"
	else
		python "$@"
	fi
}

if [ ! -x "${MCPPATH}"/cleanup.sh ]; then
  echo "The path "${MCPPATH}" does not appear to be a correctly setup MCP directory."
  exit 1
fi

echo ${BASEPATH} ${FORGEPATH} ${MCPPATH} ${JARS}

if [ "$1" = "-skipdecompile" ]; then
  pushd "${MCPPATH}" >/dev/null
  "${MCPPATH}"/updatenames.sh -f
	popd >/dev/null
else
  if [ ! -f "${MCPPATH}"/runtime/bin/fernflower.jar ]; then
      python "${FORGEPATH}"/download_fernflower.py
  fi
  if [ ! -f "${MCPPATH}"/runtime/bin/fernflower.jar ]; then
    echo "Failed to download fernflower, install it manually and re-run setup."
    exit 1
  fi

  MCP="$(find "${BASEPATH}"jars/ -maxdepth 1 -iname 'mcp*.zip')"

  # file exists and has size > 0
  if [ ! -s "${BASEPATH}"jars/minecraft_server.jar ] ; then
    echo "Please copy minecraft_server.jar to the jars/ folder."
    exit 1
  fi
  if [ ! -d "${BASEPATH}"jars/bin/ ] || [ ! -d "${BASEPATH}"jars/resources/ ] ; then
    echo "Please copy your .minecraft/ bin and resources folders to the jars/ folder."
    exit 1
  fi

  rm -rf "${MCPPATH}" || echo "INFO: No old MCP to delete"
  7z x -o"${MCPPATH}" "${MCP}"

  cp -a "${BASEPATH}"jars/minecraft_server.jar "${BASEPATH}"jars/bin/ "${BASEPATH}"jars/resources/ "${MCPPATH}"/jars/

  rm -rf "${MCPPATH}"/src
  
  pushd "${MCPPATH}" > /dev/null
    "${MCPPATH}"/decompile.sh
  popd >/dev/null
fi  

rm -rf "${MCPPATH}"/src_base "${MCPPATH}"/src_work

find "${MCPPATH}"/src -name '*.java' -print0 | xargs -0 sed -i "s/$(printf '\r\n')\$/$(printf '\n')/"
cp "${FORGEPATH}"/MLProp.java "${MCPPATH}"/src/minecraft/net/minecraft/src/MLProp.java
cp "${FORGEPATH}"/MLProp.java "${MCPPATH}"/src/minecraft_server/net/minecraft/src/MLProp.java

pushd "${MCPPATH}" > /dev/null
  "${MCPPATH}"/updatemd5.sh -f
popd > /dev/null

cp -a "${MCPPATH}"/src "${MCPPATH}"/src_base
cp -a "${MCPPATH}"/src "${MCPPATH}"/src_work

pushd "${MCPPATH}"/src_work > /dev/null
  for i in $(find "${FORGEPATH}"/patches/ -type f -name '*.patch'); do
    patch -p2 -i "${i}"
  done
popd > /dev/null
