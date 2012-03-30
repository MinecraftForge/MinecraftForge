#!/bin/bash

echo "Forge Mod Loader Linux Setup Program"
echo 

if [ ! -f ../runtime/bin/fernflower.jar ]
then
    python download_fernflower.py
fi

if [ ! -f ../runtime/bin/fernflower.jar ]
then
    echo "Failed to download fernflower, install it manually and re-run setup."
    exit 1
fi

pushd .. > /dev/null

rm -rf conf 
mkdir conf
cp -r fml/conf/* conf

./cleanup.sh
if [ -d "src" ]
then
    echo "Failed to cleanup the MCP folder, please backup your modified files and run this script again and say yes when prompted."
    exit 1
fi
./decompile.sh

pushd src > /dev/null
    if [ -f ../jars/bin/minecraft.jar ];
    then
        for i in `find ../fml/patches/minecraft/ -type f`
        do
            patch -p2 -i $i
        done
        cp -r ../fml/src/minecraft/* minecraft
    fi


    if [ -f ../jars/minecraft_server.jar ];
    then
        for i in `find ../fml/patches/minecraft_server/ -type f`
        do
            patch -p2 -i $i
        done
        cp -r ../fml/src/minecraft_server/* minecraft_server
    fi
popd > /dev/null

./updatemcp.sh -f
./updatenames.sh -f
./updatemd5.sh -f
