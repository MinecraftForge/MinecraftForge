#!/usr/bin/env bash

for file in $(find repo | grep --invert-match -e md5 -e sha | grep -e .jar -e .zip); do
    file_path=$(realpath $file)
    file_name=$(basename $file)
    
    echo "$file_name"
    files=$(unzip -l $file | awk '{print $4}')
    for f in $files; do
        out="_actual/$file_name/$f"
        parent=$(dirname "$out")
        echo $out
        
        if [ ! -d "$parent" ]; then
            mkdir -p "$parent"
        fi
            
        if [[ $f == *.class ]]; then
            javap -v -p jar:file://$file_path!/$f | tail -n +4 >$out.txt
        elif [[ $f != */ && $f != "Name" && $f != "----" ]]; then
            unzip -o -q $file $f -d _actual/$file_name
        fi
    done
done