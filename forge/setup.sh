if [ ! -d ../src ]; then
   move ../src_base src
   rm -rf src_work
fi

cp -r ../src ../src_base
cp -r ../src ../src_work
dir=`pwd`
cd ../src_work
patch -u -p2 < $dir/minecraft.patch
