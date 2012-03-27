fdir=$PWD

python update_patches.py
echo Grabbing copy of conf folder
for i in `find $fdir/conf -type f | grep -a -v ".gitignore"` 
do 
	rm $i 
done
for i in `find $fdir/conf -depth -empty -type d | grep -a -v ".git"` 
do 
	rmdir "$i"
done

mkdir -p $fdir/conf
cp -r conf/* $fdir/conf

echo Finished
