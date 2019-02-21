#!/bin/sh
pg_dump  --username=cuba -W --file=rtneo.sql -h localhost rtneo
