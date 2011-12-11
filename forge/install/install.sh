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
    cp ../forge/MLProp.java minecraft/net/minecraft/src/MLProp.java
    cp ../forge/MLProp.java minecraft_server/net/minecraft/src/MLProp.java
    
    find ../forge/ -name *.patch -exec sed -i 's/\r//g' \{\} \;
    patch -p2 -i ../forge/modLoaderMP.patch
    
    for i in `find ../forge/patches/ -type f`
    do
        patch -p2 -i $i
    done
popd > /dev/null

cp -r forge/src/* src


./updatemcp.sh
./updatenames.sh
./updatemd5.sh