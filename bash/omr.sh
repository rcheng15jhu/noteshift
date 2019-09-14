#!/bin/bash

cd /home/shintyl/audiveris/

./gradlew run -PcmdLineArgs="-batch,-export,-output,/var/www/html/mxl/original,--,/var/www/html/uploads/"$1
