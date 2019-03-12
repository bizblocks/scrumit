#!/bin/bash

projname=scrumit
today=$(date +%Y%m%d)

scripts/dumpdb.sh $projname
pushd ..

mkdir backup/$today
echo Copying project, see log 
cp -r $projname/ backup/$today 1>> backup/$today.log

echo Arc project
tar -c --xz -C backup/ -f backup/$today.tar.xz $today/ 1>> backup/$today.log

popd
