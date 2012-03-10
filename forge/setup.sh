#!/bin/bash

set -e

echo "MinecraftForge Linux Setup Program"
echo 

if which 7z >/dev/null ; then
  echo "" > /dev/null
else
  echo "You need to install 7z, typically this is in p7zip-full or something similar"
  exit 2
fi

SETUP_SCRIPT=$(readlink -e $0)
FORGEPATH="${SETUP_SCRIPT%/*}"
BASEPATH="${FORGEPATH%/*}"
MCPPATH="${BASEPATH}/MCP"
LIBS="${BASEPATH}/libs"

python () {
	if which python2 > /dev/null ; then
		python2 "$@"
	else
		python "$@"
	fi
}

export -f python

if [ ! -x "${MCPPATH}"/cleanup.sh ]; then
  echo "The path "${MCPPATH}" does not appear to be a correctly setup MCP directory."
  exit 1
fi

echo ${BASEPATH} ${FORGEPATH} ${MCPPATH} ${LIBS}
echo

if [ "$1" = "-skipdecompile" ]; then
  pushd "${MCPPATH}" >/dev/null
  "${MCPPATH}"/updatenames.sh -f
	popd >/dev/null
else
  if [ ! -f "${BASEPATH}"/runtime/bin/fernflower.jar ]; then
      python "${FORGEPATH}"/download_fernflower.py
  fi
  if [ ! -f "${BASEPATH}"/runtime/bin/fernflower.jar ]; then
    echo "Failed to download fernflower, install it manually and re-run setup."
    exit 1
  fi

  MCP="$(find "${LIBS}" -maxdepth 1 -iname 'mcp*.zip')"

  # file exists and has size > 0
  if [ ! -s "${LIBS}"/minecraft_server.jar ] ; then
    echo "Please copy a CLEAN minecraft_server.jar to the ${LIBS} folder."
    exit 1
  fi
  if [ ! -d "${LIBS}"/bin/ ] || [ ! -d "${LIBS}"/resources/ ] ; then
    echo "Please copy a CLEAN .minecraft/ bin and resources folders to the ${LIBS} folder."
    exit 1
  fi

  CLI_ML="$(find "${LIBS}"   -iname 'ModLoader[^Mm]*zip')"
  CLI_MLMP="$(find "${LIBS}" -iname 'ModLoaderMP*[^r].zip')"
  SRV_MLMP="$(find "${LIBS}" -iname 'ModLoaderMP*Server.zip')"
  if [ ! -s "${CLI_ML}" ] || [ ! -s "${CLI_MLMP}" ] || [ ! -s "${SRV_MLMP}" ] ; then
    echo "Please make sure there are ModLoader and ModLoaderMP files in your ${LIBS} folder."
    exit 1
  fi

  rm -rf "${MCPPATH}" || echo "INFO: No old MCP to delete"
  7z x -o"${MCPPATH}" "${MCP}"

  mkdir -p "${MCPPATH}/runtime/bin"
  ln "${BASEPATH}/"runtime/bin/fernflower.jar "${MCPPATH}/runtime/bin/"

  cp -a "${LIBS}"/minecraft_server.jar "${LIBS}"/bin/ "${LIBS}"/resources/ "${MCPPATH}"/jars/
  mkdir tmp
  cd tmp
  7z x "${CLI_ML}"
  7z x "${CLI_MLMP}"
  7z a "${MCPPATH}/jars/bin/minecraft.jar" ./
  rm -rf ../tmp/*
  7z x "${SRV_MLMP}"
  7z a "${MCPPATH}/jars/minecraft_server.jar" ./
  cd ..
  rm -rf tmp

  rm -rf "${MCPPATH}"/src

  # Use Forge fields.csv
  cp -a "${MCPPATH}/conf" "${LIBS}/mcp_src_conf"

  pushd "${MCPPATH}" > /dev/null
    yes Yes | bash "${MCPPATH}/updatemcp.sh"
  popd >/dev/null
  mv "${MCPPATH}/conf" "${LIBS}"/mcp_upd_conf"

  cp -a "${FORGEPATH}/conf" "${MCPPATH}/conf"
  
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
  for i in $(find "${FORGEPATH}"/patches/ -type f -name '*.patch' | sort); do
    patch -p2 -i "${i}"
  done
popd > /dev/null
