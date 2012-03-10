#!/bin/sh
MCP=$(find src/ -maxdepth 1 -iname 'mcp*.zip')

# file exists and has size > 0
if [ ! -s src/minecraft_server.jar ] ; then
	echo "Please copy minecraft_server.jar to the src/ folder."
	exit 1
fi
if [ ! -d src/bin/ ] || [ ! -d src/resources/ ] ; then
	echo "Please copy your .minecraft/ bin and resources folders to the src/ folder."
	exit 1
fi

rm -rf MCP || echo "INFO: No old MCP to delete"
7z x -oMCP/ "${MCP}"

cp -a src/minecraft_server.jar src/bin/ src/resources/ MCP/jars/

cd MCP

python () { 
	python2 "$@"
}

export -f python

yes Yes | bash updatemcp.sh
bash decompile.sh

# && \
#bash 

