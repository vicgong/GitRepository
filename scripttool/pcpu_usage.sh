#!/bin/bash
#文件名：pcpu_usage.sh
#用途：计算1小时内cpu使用情况
#监控时间
check_time=240
sect=60
#执行次数
count=$(( $check_time / $sect ))
echo Watching CPU usage
for((i=1;i<count;i++)); do
   ps -eo comm,pcpu | tail -n +2 >> /tmp/pcpu_usage.$$
   sleep $sect
done
echo "CPU结果："
declare -A list;
cat /tmp/pcpu_usage.$$ | awk '{list[$1]+=$2;} \
END{
for(i in list)
{
printf("%s\t%d\n",i,list[i]); 
}
}' | sort -nrt 2 | head

