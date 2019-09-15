#!/bin/bash

export QT_QPA_PLATFORM=offscreen

inotifywait -m /var/www/html/uploads/ -e create -e moved_to |
    
    while read path action file; do
	
        echo "The file '$file' appeared in directory '$path' via '$action'"

	./omr.sh $file
	./transpose.sh $file
	./musescore.sh $file
	
    done

