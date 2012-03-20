read major minor revision build <version.txt
version=$major.$minor.$revision.$build
build_dir=`pwd`
dir=`pwd`/../forge-$version

function package_all () {
   qual=$1
   
   echo "=================================== MinecraftForge$qual-$version.zip Start ================================="
   
   cp "$build_dir/minecraftforge_credits.txt" .
   
   zip -r "$dir/minecraftforge$qual-$version.zip" .
   
   echo "=================================== MinecraftForge$qual-$version.zip Finished ================================="
}

cd ../reobf

if [ ! -d "minecraft" -o ! -d "minecraft_server" ]; then
    echo "Reobfusication failed, output directories do no exist."
    exit 1
fi

rm -rf "$dir"
mkdir "$dir"

cd minecraft
package_all "-client"

cd ../minecraft_server
package_all "-server"

cd ../..

rm -rf reobf

cd "$build_dir"

echo "=================================== MinecraftForge-src-$version.zip Start ================================="
mkdir forge
cd forge
mkdir src
mkdir src/minecraft
mkdir src/minecraft_server
mkdir patches
mkdir conf
cp -r ../forge_client/src/* src/minecraft
cp -r ../forge_server/src/* src/minecraft_server
cp -r ../forge_common/* src/minecraft
cp -r ../forge_common/* src/minecraft_server
cp -r ../patches/* patches
cp -r ../conf/* conf
cp ../lfcr.py .
cp ../clean_src.py .
cp ../install/install.cmd .
cp ../install/install.sh .
cp ../download_fernflower.py .
cp ../install/README.txt .
cp ../minecraftforge_credits.txt .

cd ..
sh inject_version.sh forge/src/minecraft/net/minecraft/src/forge/ForgeHooks.java
sh inject_version.sh forge/src/minecraft_server/net/minecraft/src/forge/ForgeHooks.java

zip -r "$dir/minecraftforge-src-$version.zip" forge
rm -rf forge

echo "=================================== MinecraftForge-src-$version.zip Finished ================================="
