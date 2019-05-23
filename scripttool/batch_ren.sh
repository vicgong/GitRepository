#!/bin/sh
# Filename: batch_ren.sh
# we have less than 3 arguments. Print the help text:
# $# 表示命令行参数的个数
if [ $# -lt 3 ] ; then 
# 从文件中重定向到命令cat中
cat <<HELP
batch_ren -- renames a number of files using sed regular expressions
USAGE: sh batch_ren.sh 'regexp' 'replacement' files...
EXAMPLE: rename all *.HTM files in *.html:
sh batch_ren.sh 'HTM$' 'html' *.HTM
HELP
exit 1
fi
OLD="$1"
NEW="$2"
# The shift command removes one argument from the list of 
# shift n命令用于参数列表删除n个参数(缺省n时默认删除一个参数)
# command line arguments.
shift
shift
# $* contains now all the files:
for file in $*; do
	if [ -f "$file" ] ; then
		newfile=`echo "$file" | sed "s/${OLD}/${NEW}/g"`
		if [ -e "$newfile" ]; then
			echo "ERROR: $newfile exists already"
		else
			echo "renaming $file to $newfile ..."
			mv "$file" "$newfile"
		fi
	fi
done

