#!/bin/bash
#文件名：filestat.sh
if [ $# -ne 1 ] ; then
    echo $0 basepath
    exit 1
fi
path=$1
declare -A statarry
while read line ; do
    ftype=`file -b "$line"`
    let statarry["$ftype"]++;
done < <(find $path -type f -print)
echo ============== file types and counts ==============
for ftype in "${!statarry[@]}" ; do
	echo $ftype : ${statarry["$ftype"]}
done
# 其中<(find $path -type f -print)等同于 filename
# 只不过使用子进程代替文件名
# 执行方式不能使用：shfilestat.sh /path
# 使用子进程时不能再原有的子进程中再开启子进程了
# 执行方式必须使用 : ./filestat.sh /path
