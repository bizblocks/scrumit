#!/bin/sh

project=$1
today=$(date +%Y%m%d-%H%M)
echo dumping database
pg_dump  --username=cuba -W --file=$project.sql -h localhost $project
rm $project.sql.xz
echo archiving
xz $project.sql
cp $project.sql.xz $today.sql.xz

