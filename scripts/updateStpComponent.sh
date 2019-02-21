#!/bin/bash

if [ -z "$1" ]; then
    echo  "Usage: $0 component"
    exit
fi

component=$1

pushd ../$component

scripts/update.sh

popd
