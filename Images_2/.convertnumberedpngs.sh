#!/bin/bash
basedir=$1
pngstorage=png-storage
dir=$pngstorage/$basedir-png

i=0;
for file in $(ls $1/*.jpg 2>/dev/null)
do
	printf -v numberedfile "%04d" $i
	cp $file ${dir}/${numberedfile}.jpg
	i=$((i+1))
done


for file in $(ls $1/*.png 2>/dev/null)
do
	printf -v numberedfile "%04d" $i
	cp $file ${dir}/${numberedfile}.png
	i=$((i+1))
done
