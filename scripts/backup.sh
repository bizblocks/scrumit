#!/bin/sh

if [ -z "$1" ]; then
    echo  "Usage: $0 projectName"
    exit
fi


projname=$1
today=$(date +%Y%m%d)

cd $projname
./dumpdb.sh
cd ..

mkdir backup/$today
echo Copying project, see log 
cp -r $projname/ backup/$today 1>> backup/$today.log

echo Arc project
tar -c --xz -C backup/ -f backup/$today.tar.xz $today/ 1>> backup/$tday.log