version=$1
build_dir=`pwd`
dir=`pwd`/../forge-$version

function remove_svn () {
  (
  cd $1

  if [ -d .svn ]; then
     rm -rf .svn
  fi

  for j in `ls`
  do
     if [ -d $j ]; then
        remove_svn $j
     fi
  done
  )
}

function package_all () {
   qual=$1

   cp $build_dir/minecraftforge_credits.txt .

   zip -r $dir/minecraftforge$qual-$version.zip \
      minecraftforge_credits.txt \
      *.class \
      forge
}

cd ../reobf

remove_svn .

rm -rf $dir
mkdir $dir

cd minecraft
package_all "-client"

cd ../minecraft_server
package_all "-server"

cd ../..

rm -rf reobf

cd $build_dir

mkdir forge
cd forge
mkdir src
mkdir src/minecraft
mkdir src/minecraft_server
cp -r ../forge_client/src/* src/minecraft
cp -r ../forge_server/src/* src/minecraft_server
cp -r ../forge_common/* src/minecraft
cp -r ../forge_common/* src/minecraft_server
cp ../minecraft.patch .
cp ../lfcr.py .
cp ../install/install.cmd .
cp ../install/README.txt .
cp ../minecraftforge_credits.txt .
cp -r ../doc .

cd src
remove_svn .
cd ..

cd ..

zip -r $dir/minecraftforge-src-$version.zip forge
rm -rf forge
