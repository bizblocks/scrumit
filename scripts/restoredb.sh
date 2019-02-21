#!/bin/bash

project=$1

sudo -u postgres psql -c "drop database $1 ;" 
sudo -u postgres psql -c "create database $1 with owner='cuba';" 
sudo -u postgres psql rtneo -f $1.sql

