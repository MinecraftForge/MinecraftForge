#!/bin/bash

set -e
echo Update Patches, with die on unhandled error mode set.

echo Creating directories

fdir=$PWD
cd ..

for i in `find src_work -type d`
do 
	mkdir --parents "$fdir/patches${i##src_work}"
done

echo Creating patches

cd src_work
for i in `find ../src_work -type f`
do
	ibase=${i##../src_work}
	diff -u ../src_base$ibase $i -r --strip-trailing-cr --new-file | sed -e "1,2s/[0-9-]* [0-9:\.]* [+-][0-9]*\b/0000-00-00 00:00:00.000000000 -0000/" | tr -d \r > $fdir/patches$ibase.patch
done
cd ..

echo Removing empty patches
find $fdir/patches -size 0 -type f -name \*.patch -delete
for i in `find $fdir/patches -depth -empty -type d | grep -a -v "svn"` 
do 
	rmdir "$i"
done

echo Grabbing copy of conf folder
for i in `find $fdir/conf -type f | grep -a -v "svn"` 
do 
	rm $i 
done
for i in `find $fdir/conf -depth -empty -type d | grep -a -v "svn"` 
do 
	rmdir "$i"
done

mkdir -p $fdir/conf
cp -r conf/* $fdir/conf

set +e

echo Finished
