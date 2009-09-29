#!/bin/bash

date2stamp () {
	date +%s
}

dte1=$(date2stamp date)
echo $dte1

dateDiff () {
	diffSec=$(($3 - $2))
	unit=''
	case $1 in
		-s)	sec=1;	unit=s; shift;;
		-m) sec=60;	unit=m; shift;;
		-h) sec=3600: unit=h; shift;;
		-d) sec=86400; unit=d; shift;;
		*) sec=86400; unit=d;;
	esac
	if ((diffSec < 0));	then	abs=-1;	else 	abs=1;	fi
	echo "Total time : '$((diffSec/sec*abs))'$unit"
}

echo "Create new html page"

if [ $# !=  1 ];
then
	echo "no pdf files given"
	exit 0
fi

if ! [ -f "$1" ];
then
	echo "'$1' is not a file or doesn't exist !!"
	exit 0
fi

#± Check if the pdf file is valid
file=$1
ext=${file##*.}
echo "Extension : $ext"
pdfExt="pdf"
if ! [ $ext = $pdfExt ];
then
	echo "No PDF file"
	exit 0
fi

basename=${file##*/}
echo "Filename : $basename"

name=${basename%.*}
echo "Name : $name"

if [ ! -d "$name" ];
then
	echo "Create '$name' directory"
	mkdir $name
else
	echo "The '$name' directory exists!"
	rm -rf $name/*
fi

pdftohtml -c $file $name/$name

mv $name-outline.html $name

dte2=$(date2stamp date)
echo $dte2

dateDiff -s $dte1 $dte2