#!/bin/sh
projname=scrumit

git pull
scripts/updateStpComponent.sh workflow-stp

scripts/dumpdb.sh $projname
./gradlew stop assemble deploy updateDb start
