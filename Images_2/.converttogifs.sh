#!/bin/bash
basedir=$1
gifstorage=gif-storage
dir=$gifstorage/$basedir-gif

for file in $(ls $1/*.jpg 2>/dev/null)
do
	newfile=$dir/$(basename $file .jpg).gif
	if [ ! -f $newfile ]
	then
		echo -n '     Converting '
		echo -n $newfile
		echo '...'
		convert $file $newfile
	fi
done

for file in $(ls $1/*.png 2>/dev/null)
do
	newfile=$dir/$(basename $file .png).gif
	if [ ! -f $newfile ]
	then
		echo -n '     Converting '
		echo -n $newfile
		echo '...'
		convert $file $newfile
	fi
done
