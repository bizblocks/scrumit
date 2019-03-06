#!/bin/sh
pg_dump  --username=cuba -W --file=$1.sql -h localhost $1
