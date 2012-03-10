#!/bin/bash

set -e


python () {
	if which python2 > /dev/null ; then
		python2 "$@"
	else
		python "$@"
	fi
}

# bash is required if python3 is installed.
export -f python


SETUP_SCRIPT=$(readlink -e $0)
FORGEPATH="${SETUP_SCRIPT%/*}"
BASEPATH="${FORGEPATH%/*}"
MCPPATH="${BASEPATH}/MCP"
LIBS="${BASEPATH}/libs"

if [ ! -s "${BASEPATH}"/runtime/bin/fernflower.jar ]; then
    python "${FORGEPATH}"/download_fernflower.py
fi
if [ ! -s "${BASEPATH}"/runtime/bin/fernflower.jar ]; then
  echo "Failed to download fernflower, install it manually and re-run mcpsetup."
  exit 1
fi

MCP="$(find "${LIBS}" -maxdepth 1 -iname 'mcp*.zip')"
if [ ! -s "${MCP}" ] ; then
  echo "mcp not found in ${LIBS}"
  exit 1
fi

rm -rf "${MCPPATH}" || echo "INFO: No old MCP to delete"
mkdir -p "${MCPPATH}"

7z x -o"${MCPPATH}" "${MCP}" || unzip "${MCP}" -d "${MCPPATH}"

mkdir -p "${MCPPATH}/runtime/bin"
ln "${BASEPATH}/"runtime/bin/fernflower.jar "${MCPPATH}/runtime/bin/"

# Use Forge fields.csv
rm -rf "${LIBS}/mcp_src_conf" "${LIBS}/mcp_upd_conf"

cp -a "${MCPPATH}/conf" "${LIBS}/mcp_src_conf"

pushd "${MCPPATH}" > /dev/null
  "${MCPPATH}/updatemcp.sh" -f
popd >/dev/null
mv "${MCPPATH}/conf" "${LIBS}/mcp_upd_conf"

cp -a "${FORGEPATH}/conf" "${MCPPATH}/conf"

echo
echo "Ready for update_jars.sh and/or setup.sh"
echo
echo
