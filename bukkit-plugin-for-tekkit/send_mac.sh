#!/bin/bash

FILE="/Users/ianwilkey/Desktop/teknet/teknet-core/target/TeknetCore-LATEST.jar"

dev=0

echo "Listening for update..."
while [ "1" == "1" ]
do
	if test -f "$FILE"; then
		# ./delete-old.exp TeknetCore-v1.100.$dev.jar
		sleep 2
		((dev+=1))
		mv $FILE TeknetCore-v1.100.$dev.jar
	    scp TeknetCore-v1.100.$dev.jar 150.136.80.254:/home/opc/legends/plugins
	    rm TeknetCore-v1.100.$dev.jar
	    echo "Sent new TeknetCore update to target."
	    echo "Listening for update..."
	fi
done
