#!/bin/sh

read major minor rev build <version.txt

while [ -n "$1" ] ; do
  echo Injecting Revision $major.$minor.$rev.$build $1
  sed -i "s/$(printf '\r\n')\$/$(printf '\n')/;s/majorVersion=0/majorVersion=$major/;s/minorVersion=0/minorVersion=$minor/;s/revisionVersion=0/revisionVersion=$rev/;s/buildVersion=0/buildVersion=$build/" "$1"
  shift
done