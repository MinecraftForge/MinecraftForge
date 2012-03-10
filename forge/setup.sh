#!/bin/bash

set -e

echo "MinecraftForge Linux Setup Program"
echo 

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
  echo "Set it up manually or run ${FORGEPATH}/mcpsetup.sh"
  exit 1
fi

echo ${BASEPATH} ${FORGEPATH} ${MCPPATH} ${LIBS}
echo

if [ "$1" != "-f" ] ; then
  if [ "$1" = "-skipdecompile" ]; then
    pushd "${MCPPATH}" >/dev/null
      "${MCPPATH}"/updatenames.sh -f
    popd >/dev/null
  else
    pushd "${MCPPATH}" > /dev/null
      if [ ! -e ./runtime/bin/fernflower.jar ] ; then
        echo "MCP/runtime/bin/fernflower.jar not found, setup manually and use -skipdecompile or -f instead"
      fi
      "${MCPPATH}"/decompile.sh
    popd >/dev/null
  fi
fi

find "${MCPPATH}"/src -name '*.java' -print0 | xargs -0 sed -i "s/$(printf '\r\n')\$/$(printf '\n')/"

rm -rf "${MCPPATH}"/src_base "${MCPPATH}"/src_work

pushd "${MCPPATH}" > /dev/null
  "${MCPPATH}"/updatemd5.sh -f
popd > /dev/null

cp -a "${MCPPATH}"/src "${MCPPATH}"/src_base
cp -a "${MCPPATH}"/src "${MCPPATH}"/src_work

pushd "${MCPPATH}"/src_work > /dev/null
  find "${FORGEPATH}"/patches/ -type f -name '*.patch' | sort | while read FILE ; do
    patch -p2 -i "${FILE}"
  done
popd > /dev/null
