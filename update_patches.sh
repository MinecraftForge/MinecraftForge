rm -rf patches

echo Creating directories

for i in `find ../src_work -type d`
do 
	mkdir --parents "./patches${i:11}"
done

echo Creating patches

for i in `find ../src_work -type f`
do
	diff -u ../src_base${i:11} $i -r --strip-trailing-cr --new-file | sed -e "1,2s/[0-9-]* [0-9:\.]* [+-][0-9]*\b/0000-00-00 00:00:00.000000000 -00/" | tr -d '\r' > ./patches${i:11}.patch
done

echo Removing empty patches
for i in `find patches -size 0 -type f` 
do 
	rm $i 
done
for i in `find patches -depth -empty -type d` 
do 
	rmdir "$i"
done

