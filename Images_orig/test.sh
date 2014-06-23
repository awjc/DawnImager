SRCDIR=/mnt/csdrive/Dawn/Images/vesta
DESTDIR=~/Dropbox/Java_Projects/DawnImager/Images/vesta

for i in $(ls $SRCDIR/*.png)
do
	SAME=NO
	for j in $(ls $DESTDIR/*.png)
	do
		DIF=$(cmp $i $j)
		if [ -z "$DIF" ]
		then
			SAME=YES
		fi
	done

	if [ "$SAME" != "YES" ]
	then
		echo $i
	fi
done
