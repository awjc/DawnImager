rm MOVS/*
i=0
for file in $(ls *.png)
do
	echo -n 'Working on '
	echo -n $file
	echo '...'

	if [ $i -lt 30 ]
	then
		nfiles=3
	elif [ $i -lt 85 ]
	then
		nfiles=3
	elif [ $i -lt 145 ]
	then
		nfiles=5
	else
		nfiles=8
	fi

	for j in $(seq 0 $((nfiles - 1)))
	do
		cp $file MOVS/$(printf %02d $j).png
	done

	ffmpeg -y -i MOVS/%02d.png -sameq -r 60 MOVS/$(printf %04d $i).mpg >/dev/null 2>/dev/null
	i=$((i+1))

	rm -f MOVS/*.png
done

cat MOVS/*.mpg > final.mpg
ffmpeg -y -sameq -i final.mpg finaltmp.mp4 >/dev/null 2>/dev/null
ffmpeg -y -sameq -i finaltmp.mp4 -vf "setpts=0.5*PTS" final.mp4 >/dev/null 2>/dev/null

rm -f final.mpg
rm -f finaltmp.mp4
