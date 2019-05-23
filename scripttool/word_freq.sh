#!/bin/bash
#文件名：word_freq.sh
#用途：统计文件中单词出现的词频
if [ $# -ne 1 ]; then 
   echo please input filename
exit 1
fi
filename=$1
declare -A arr
grep -E -o "\b[a-zA-Z]+\b" $filename | \
awk '
BEIGN{ print word"\t"count} 
{ arr[$0]++; } 
END{ for(i in arr) { print i"\t"arr[i]} }'

