#!/bin/bash

if [ ! -z $(which fswatch) ]
then
	./css.sh
	fswatch -o -1 ./src/scss | xargs -n1 ./css.sh
else
	while true; do
		./css.sh
		inotifywait src/scss
	done
fi
