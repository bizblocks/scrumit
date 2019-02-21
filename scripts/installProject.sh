#!/bin/bash

if [ "$1"=="" ]; then
    echo  "Usage: $0 projectName"
    exit
fi

project=$1
git clone https://github.com/bizblocks/$project

pushd $project
./installComponents.sh
