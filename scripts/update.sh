#!/bin/sh
git pull
scripts/updateStpComponent.sh workflow-stp

scripts/dumpdb.sh
./gradlew stop assemble deploy updateDb start
