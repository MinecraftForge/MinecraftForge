#!/bin/bash

target=$1
read major minor rev build <version.txt

echo Injecting Revision $major.$minor.$rev.$build $target

sed s/majorVersion=0/majorVersion=$major/ <$target | \
sed s/minorVersion=0/minorVersion=$minor/ | \
sed s/revisionVersion=0/revisionVersion=$rev/ | \
sed s/buildVersion=0/buildVersion=$build/ | \
tr -d '\r' > $target.tmp

mv $target.tmp $target