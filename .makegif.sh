echo -n 'Working on '
echo -n $1
echo '...'

DAWNBASE=~/Dropbox/Java_Projects/DawnImager
GIFSDIR=${DAWNBASE}/gifs

DIR=${DAWNBASE}/Images/

#navigate to DIR
cd $DIR

./.converttogifs.sh $1

cd ./gif-storage/$1-gif

#separate the last image out
LASTFILE=$(ls -rt1 | grep -G '[\.png|\.jpg|\.gif]$' | awk '{ f=$NF }; END{ print f }')
OTHERFILES=$(ls | grep -G '[\.png|\.jpg|\.gif]$' | grep -v $LASTFILE)

#make the intermediate gifs
convert -delay $2 $OTHERFILES temp1.gif
convert -delay $3 $LASTFILE temp2.gif
convert -loop 0 temp1.gif temp2.gif ${GIFSDIR}/${1}.gif

#remove intermediates
rm temp1.gif temp2.gif

echo 'DONE!'
