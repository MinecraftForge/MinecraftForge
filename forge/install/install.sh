#!/bin/bash

echo "MinecraftForge Linux Setup Program"
echo 

pushd .. > /dev/null

rm -rf conf 
mkdir conf
cp -r forge/conf/* conf

./cleanup.sh
./decompile.sh --jad

pushd src > /dev/null
    #find . -name *.java -exec sed -i 's/\r//g' \{\} \;
    #find ../forge/ -name *.patch -exec sed -i 's/\r//g' \{\} \;

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


# Removed until MCP's UpdateNames Is fixed
#./updatemcp.sh
#./updatenames.sh
./updatemd5.sh