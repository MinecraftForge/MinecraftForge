#!/bin/bash

cd ..

rm -rf src bin src_forge

cp -a src_work src_forge

cp -a forge/forge_client/src/net/* src_forge/minecraft/net/
cp -a forge/forge_server/src/net/* src_forge/minecraft_server/net/
cp -a forge/forge_common/net/* src_forge/minecraft/net/
cp -a forge/forge_common/net/* src_forge/minecraft_server/net/

pushd forge > /dev/null
./inject_version.sh $OLDPWD/src_forge/minecraft/net/minecraft/src/forge/ForgeHooks.java
./inject_version.sh $OLDPWD/src_forge/minecraft_server/net/minecraft/src/forge/ForgeHooks.java
popd > /dev/null

cp -a src_forge src

./recompile.sh


