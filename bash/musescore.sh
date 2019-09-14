#!/bin/bash

cd /var/www/html/pdf/
x=$1
y=${x%.pdf}
mscore -o 'tmp.pdf' '/var/www/html/mxl/transposed/'$y.xml

printf "mscore done"
