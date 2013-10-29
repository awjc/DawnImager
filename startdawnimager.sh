NPROCESSES=$(ps ax | grep "java -jar dawnimager.jar" | wc -l)

if [ $NPROCESSES -lt 2 ]
then
	echo "Starting Dawn Imager..."
	cd ~/Dropbox/Java_Projects/DawnImager/
	java -jar dawnimager.jar &
else
	echo "Dawn Imager is already running!"
fi
