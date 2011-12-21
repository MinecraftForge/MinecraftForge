#!/bin/bash

echo "MinecraftForge Linux Setup Program"
echo 

pushd .. > /dev/null

rm -rf conf 
mkdir conf
cp -r forge/conf/* conf

if [ -f runtime/bin/fernflower.jar ];
then
   mv runtime/bin/fernflower.jar runtime/bin/fernflower.jar-backup
fi

./cleanup.sh
./decompile.sh

if [ -f runtime/bin/fernflower.jar-backup ];
then
   mv runtime/bin/fernflower.jar-backup runtime/bin/fernflower.jar
fi


pushd src > /dev/null
    find . -name *.java -exec sed -i 's/\r//g' \{\} \;
    find ../forge/ -name *.patch -exec sed -i 's/\r//g' \{\} \;

    if [ -f ../jars/bin/minecraft.jar ];
    then
        cp ../forge/MLProp.java minecraft/net/minecraft/src/MLProp.java
    
        for i in `find ../forge/patches/minecraft/ -type f`
        do
            patch -p2 -i $i
        done
    fi


    if [ -f ../jars/minecraft_server.jar ];
    then
        cp ../forge/MLProp.java minecraft_server/net/minecraft/src/MLProp.java
    
        patch -p2 -i ../forge/modLoaderMP.patch
    
        for i in `find ../forge/patches/minecraft_server/ -type f`
        do
            patch -p2 -i $i
        done
    fi
popd > /dev/null

cp -r forge/src/* src


./updatemcp.sh
./updatenames.sh
./updatemd5.sh