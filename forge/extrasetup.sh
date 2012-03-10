#!/bin/bash


SETUP_SCRIPT=$(readlink -e $0)
FORGEPATH="${SETUP_SCRIPT%/*}"
BASEPATH="${FORGEPATH%/*}"
MCPPATH="${BASEPATH}/MCP"
LIBS="${BASEPATH}/libs"

cd "${MCPPATH}"

cp -a "${MCPPATH}"/src_work "${MCPPATH}"/src_forge 

cp -a "${FORGEPATH}"/forge_common/net/* "${MCPPATH}"/src_forge /minecraft/net/
cp -a "${FORGEPATH}"/forge_client/src/net/* "${MCPPATH}"/src_forge /minecraft/net/

cp -a "${FORGEPATH}"/forge_common/net/* "${MCPPATH}"/src_forge /minecraft_server/net/
cp -a "${FORGEPATH}"/forge_server/src/net/* "${MCPPATH}"/src_forge /minecraft_server/net/

pushd "${FORGEPATH}" > /dev/null
./inject_version.sh "${OLDPWD}/src_forge/minecraft/net/minecraft/src/forge/ForgeHooks.java"
./inject_version.sh "${OLDPWD}/src_forge/minecraft_server/net/minecraft/src/forge/ForgeHooks.java"
popd > /dev/null

mv "${MCPPATH}"/src "${MCPPATH}"/src_prior
cp -a "${MCPPATH}"/src_forge "${MCPPATH}"/src
