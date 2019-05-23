#!/bin/bash
# Filename: parallel_task_1.sh
# Function: 并行执行任务

#作业数
jobnum=10
#可同时运行的最大作业数
procnum=5
#测试命令执行
function CMD 
{
   second=$((RANDOM % 500))
   echo "第 $1 个job,sleeping $second 秒"
   sleep $second
}
#压入进程队列
function push_list 
{
   proc_list="$proc_list $1"
   run_count=$(($run_count+1))
}
#更新进程队列
function update_list
{
   old_list=$proc_list
   proc_list=""
   run_count=0
   for PID in $old_list; do
       if [[ -d /proc/$PID ]]; then
          push_list $proc_no;
       fi
   done
}
#检查进程队列
function check_list
{
   old_list=$proc_list
   for PID in $old_list; do
       if [[ ! -d /proc/$PID ]]; then
          update_list;
          break;
       fi
    done
}
#并行执行命令
for((i=1;i<=$jobnum;i++)); do
   CMD $i &
   #获取后台进程的PID
   PID=$!
   push_list $PID
   #检查是否到达最大同时运行的作业数
   while [[ $run_count -ge $procnum ]]; do
         check_list;
         sleep 1;
   done
done
wait

