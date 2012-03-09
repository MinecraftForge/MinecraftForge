#!/bin/bash

echo "MinecraftForge Linux Setup Program"
echo 

pushd .. > /dev/null

PYTHON2=$(which python)
which python2 2>/dev/null && PYTHON2=$(which python2)

if [ -d src ] ; then
	echo "!!! WARNING !!!"
	echo
	echo "This setup will erase your src directory.  Make sure to back up"
	echo "any changes before continuing."
	echo

	read -p "Continue (y/n)? " var

	if [ "$var" != "y" ]; then exit ; fi
fi

if [ ! -f ./runtime/bin/fernflower.jar ]
then
    pushd forge
        ${PYTHON2} download_fernflower.py
    popd
fi

if [ ! -f ./runtime/bin/fernflower.jar ]
then
    echo "Failed to download fernflower, install it manually and re-run setup."
    exit 1
fi

rm -rf src src_work src_forge

if [ ! -d src_base ] ; then
	./decompile.sh
	pushd src > /dev/null

	find . -name *.java -print0 | xargs -0 sed -i "s/$(printf '\r\n')\$/$(printf '\n')/"
	cp ../forge/MLProp.java minecraft/net/minecraft/src/MLProp.java
	cp ../forge/MLProp.java minecraft_server/net/minecraft/src/MLProp.java
	patch -p2 -i ../forge/modLoaderMP.patch

	popd > /dev/null

	mv src src_base
fi

cp -a src_base src_work
pushd src_work > /dev/null
  find ../forge/patches/ -type f -name \*.patch -print0 | xargs -0I, patch -p2 -i ,
popd > /dev/null

cp -a src_work src_forge

cp -a forge/forge_client/src/net/* src_forge/minecraft/net/
cp -a forge/forge_common/net/* src_forge/minecraft/net/
cp -a forge/forge_common/net/* src_forge/minecraft_server/net/
cp -a forge/forge_server/src/net/* src_forge/minecraft_server/net/

pushd forge > /dev/null
./inject_version.sh $OLDPWD/src_forge/minecraft/net/minecraft/src/forge/ForgeHooks.java
./inject_version.sh $OLDPWD/src_forge/minecraft_server/net/minecraft/src/forge/ForgeHooks.java
popd > /dev/null

cp -a src_forge src

./updatemd5.sh

