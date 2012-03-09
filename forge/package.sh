read major minor revision build <version.txt
version=$major.$minor.$revision.$build
build_dir=`pwd`
dir=`pwd`/../forge-$version

function remove_svn () {
  (
  cd "$1"

  [ -d .svn ] && rm -rf .svn

  find ./ -maxdepth 1 -type d -print0 | xargs -0I, remove_svn ,
  )
}

function package_all () {
   qual=$1

   cp "$build_dir/minecraftforge_credits.txt" .

   echo "Making minecraftforge$qual-$version.zip"
   
   zip -r "$dir/minecraftforge$qual-$version.zip" .
}

cd ../reobf

remove_svn .

rm -rf "$dir"
mkdir "$dir"

cd minecraft
package_all "-client"

cd ../minecraft_server
package_all "-server"

cd ../..

rm -rf reobf

cd "$build_dir"

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
cp ../install/install.cmd .
cp ../install/install.sh .
cp ../download_fernflower.py .
cp ../MLProp.java .
cp ../install/README.txt .
cp ../minecraftforge_credits.txt .
cp -r ../doc .

cd ..
sh inject_version.sh forge/src/minecraft/net/minecraft/src/forge/ForgeHooks.java
sh inject_version.sh forge/src/minecraft_server/net/minecraft/src/forge/ForgeHooks.java
cd forge 

cd src
remove_svn .
cd ../patches
remove_svn .
cd ../conf
remove_svn .
cd ..

cd ..

zip -r "$dir/minecraftforge-src-$version.zip" forge
rm -rf forge
