#!/bin/bash

pushd ..

rm -rf src
cp -a src_work src
rm -rf bin

popd

cp -a forge_client/src/net/* ../src/minecraft/net/
cp -a forge_common/net/* ../src/minecraft/net/
cp -a forge_common/net/* ../src/minecraft_server/net/

pushd ..

./recompile.sh


