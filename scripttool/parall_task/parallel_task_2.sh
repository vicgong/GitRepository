#!/bin/bash
jobnum=10       #作业数
procnum=5       #可同时运行的最大作业数
#测试命令执行
function CMD
{
   second=$((RANDOM % 500))
   echo "第 $1 个job,sleeping $second 秒"
   sleep $second
}
#以PID名称创建FIFO文件，防止创建的命名管道时与已有文件重名，从而失效
pfifo=$$.fifo
#创建命名管道
mkfifo $pfifo
#以文件描述符fd6，读写的方式打开命名管道
exec 6<>$pfifo
#删除文件，也可以不删除，不影响后续操作
rm -f $pfifo
#在fd6中放置$procnum个z字符作为令牌
for((i=1;i<=$procnum;i++)); do
   echo 'z'
done >&6
#依次提交作业，领取令牌，即从fd6读取行，每次读取一行
for((i=1;i<=$jobnum;i++)); do
   #对管道每次读取一行便少一行，每次只能读取一行
   #直到所有的行读取完毕，执行挂起，直到管道再有可读行
   read <&6
   #批量执行的命令放在大括号内，后台运行
  {
    CMD $i && {  #判断进程执行的成功与否
    echo "job $i finished"
    } || {
       echo "job $i error"
    }
#暂停1秒，关键点
#给系统缓冲时间，达到限制并行进程的数量的作用
sleep 1
echo 'z' >&6
} &
done
#等待所有后台进程结束
wait
#删除文件描述符
exec 6>&-

