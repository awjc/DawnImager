#!/bin/bash

cd ~/Dawn/Images/$1
LASTFILE=$(ls -rt1 | grep -G '[\.png|\.jpg|\.gif]$' | awk '{ f=$NF }; END{ print f }')

echo $LASTFILE
