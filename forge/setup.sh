#!/bin/bash

echo "MinecraftForge Linux Setup Program"
echo 

pushd .. > /dev/null

if [ -d src ] ; then
	echo "!!! WARNING !!!"
	echo
	echo "This setup will erase your src directory.  Make sure to back up"
	echo "any changes before continuing."
	echo

	read -p "Continue (y/n)? " var

	if [ "$var" != "y" ]; then exit ; fi
fi

rm -rf src src_work src_forge

if [ ! -d src_base ] ; then
	./decompile.sh
	pushd src > /dev/null

	find . -name *.java -exec sed -i 's/\r//g' \{\} \;
	patch -p2 -i ../forge/modLoaderMP.patch
	patch -p1 -i ../forge/mlprop.patch

	popd > /dev/null

	mv src src_base
fi

cp -a src_base src_work
pushd src_work > /dev/null
	patch -p2 -i ../forge/minecraft.patch
popd > /dev/null

cp -a src_work src_forge

cp -a forge/forge_client/src/net/* src_forge/minecraft/net/
cp -a forge/forge_common/net/* src_forge/minecraft/net/
cp -a forge/forge_common/net/* src_forge/minecraft_server/net/

cp -a src_forge src

./updatemd5.sh

