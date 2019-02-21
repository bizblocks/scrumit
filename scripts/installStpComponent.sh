#!/bin/bash

if [ -z "$1" ]; then
    echo  "Usage: $0 component"
    exit
fi

component=$1

pushd ..

git clone https://github.com/bizblocks/$component
pushd  $component
chmod 700 gradlew
./gradlew assemble install

popd

popd
