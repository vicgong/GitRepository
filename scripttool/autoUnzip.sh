#!/bin/bash
# FileName: autoUnzip.sh
# Function: 自动解压 bzip2, gzip 和 zip类型的压缩文件

if [ $# -ne 1 ]; then
	echo "Usage: autoUnzip.sh file"
	exit 1
fi

ftype=`file "$1"`
case "$ftype" in
"$1: Zip archive"*) unzip "$1";;
"$1: gzip compressed"*) gunzip "$1";;
"$1: bzip2 compressed"*) bunzip2 "$1";;
*) error "File $1 can not be uncompressed with smartzip";;
esac
