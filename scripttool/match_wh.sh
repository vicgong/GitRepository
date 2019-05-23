#!/bin/bash
#文件名：match_wh.sh
#用途：打印出给定长度的回文字列表
if [ $# -ne 2 ] ; then
echo please inpuit match filename and hw_length
exit 1
fi

file=$1
count=$[ $2 / 2 ]
pattern='/^\(.\)'

for((i=1;i<count;i++)) ; do
pattern=$pattern'\(.\)'
done

if [ $(( $2 % 2 )) -ne 0 ] ; then
pattern=$pattern'.'
fi

for((count;count>0;count--)) ; do
pattern=$pattern'\'"$count"
done
pattern=$pattern'$/p'
sed -n "$pattern" "$file"

