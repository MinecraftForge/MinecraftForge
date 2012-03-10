#!/bin/bash

echo "MinecraftForge Linux Setup Program"
echo 

PYTHON2=$(which python)
which python2 2>/dev/null && PYTHON2=$(which python2)

if [ ! -f ../runtime/bin/fernflower.jar ]
then
   ${PYTHON2} download_fernflower.py
fi

if [ ! -f ../runtime/bin/fernflower.jar ]
then
  echo "Failed to download fernflower, install it manually and re-run setup."
  exit 1
fi

pushd .. > /dev/null

rm -rf conf 
mkdir conf
cp -r forge/conf/* conf

./cleanup.sh
./decompile.sh

pushd src > /dev/null
    if [ -f ../jars/bin/minecraft.jar ];
    then
        cp ../forge/MLProp.java minecraft/net/minecraft/src/MLProp.java
    
        for i in `find ../forge/patches/minecraft/ -type f`
        do
            patch -p2 -i $i
        done
        cp -r ../forge/src/minecraft/* minecraft
    fi


    if [ -f ../jars/minecraft_server.jar ];
    then
        cp ../forge/MLProp.java minecraft_server/net/minecraft/src/MLProp.java
    
        patch -p2 -i ../forge/modLoaderMP.patch
    
        for i in `find ../forge/patches/minecraft_server/ -type f`
        do
            patch -p2 -i $i
        done
        cp -r ../forge/src/minecraft_server/* minecraft_server
    fi
popd > /dev/null

./updatemcp.sh -f
./updatenames.sh -f
./updatemd5.sh -f
