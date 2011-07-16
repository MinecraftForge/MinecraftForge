version=$1
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

   zip -r $dir/minecraftforge$qual-$version.zip \
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
