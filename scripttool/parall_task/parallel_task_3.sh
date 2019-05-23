#!/bin/bash
#作业数
jobnum=10
#可同时运行的最大作业数
procnum=5
#测试命令执行
function CMD {
   second=$((RANDOM % 500))
   echo "第 $1 个job,sleeping $second 秒"
   sleep $second
}
PID=()
for((i=1;i<=$jobnum;)); do
   for((j=0;j<$procnum;j++)); do
      if [[ j -gt $jobnum ]]; then
         break;
      fi
      if [[ ! ${PID[j]} ]] || ! kill -0 ${PID[j]} 2>/dev/null; then
         CMD $i &
         PID[j]=$!
         i=$((i+1))
      fi
   done
done

