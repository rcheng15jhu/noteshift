#!/bin/bash

x=$1
y=${x%.pdf}

cd /var/www/html/mxl/original/$y
unzip -o $y.mxl -d /var/www/html/mxl/original/
rm -rf /var/www/html/mxl/original/META-INF/

cd /var/www/java/
java TransposeComputation $1

printf "transpose done"
