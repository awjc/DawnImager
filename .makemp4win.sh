echo -n 'Working on '
echo -n $1
echo '...'

DAWNBASE=$a/Dropbox/Java_Projects/DawnImager
MOVDIR=${DAWNBASE}/movies
MOVDIRWIN=C:/Users/Adam/Dropbox/Java_Projects/DawnImager/movies

TMPDIR=~/.temp/
DIR=${DAWNBASE}/Images

#navigate to DIR
cd $TMPDIR

#remove old files
rm -f *.morph.png

if [ $2 -ne 0 ]
then
	convert ${DIR}/$1/*.png -morph ${2} %04d.morph.png
else
	i=0
	for img in $(ls $DIR/$1/*.png)
	do
		#ln -s $img ./$(printf %04d $i).morph.png
        cp $img ./$(printf %04d $i).morph.png
		i=$((i+1))
	done	
fi

#ffmpeg -y -r ${3} -sameq -i %04d.morph.png $MOVDIR/${1}.mp4 >/dev/null 2>/dev/null
ffmpeg -y -r ${3} -i %04d.morph.png $MOVDIRWIN/${1}.mp4 -qscale 0 >/dev/null 2>/dev/null

rm -f *.morph.png
echo 'DONE!'
