#!/bin/sh
CV=`cat current_version`
echo Current version is $CV

if [ -z "$1" ] ; then 
   echo New version must be specified.
   exit
fi

echo New version is $1
SEDCMD="s/$CV/$1/"

find . -name 'pom.xml' -exec sed -i -e $SEDCMD {} \;
sed -i -e $SEDCMD current_version