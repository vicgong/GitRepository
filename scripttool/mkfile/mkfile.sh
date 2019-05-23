#!/bin/bash
#Filename: mkfile.sh
#Function: make new file with format

if [ $# -ne 2 ]; then
   echo "Usage: `basename $0` tabconf YYYYMMDD"
   exit 1
fi

conf=$1
dt=$2
dir=$(dirname $0)
mkdir -p $dir/$dt
rm -f $dir/$dt/*
for filename in `sed "s/YYYYMMDD/$dt/g" $conf`; do
    echo "[$(date +"%Y-%m-%d %X")] made $filename finished!"
    touch $dir/$dt/${filename}.dat
    gzip $dir/$dt/${filename}.dat
done
