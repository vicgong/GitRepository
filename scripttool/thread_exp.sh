#!/bin/bash  
#文件名：thread_exp.sh  
#多线程执行shell脚本。  
#v_thread_pool_size为线程数量，默认为4  
v_thread_pool_size=4  
#初始化线程池  
v_pip_file=$$.fifo  
mkfifo  $v_pip_file  
exec 8<>$v_pip_file  
rm $v_pip_file  
for i in $(seq $v_thread_pool_size)  
do  
   echo 'z'  
done >&8  
#data/20160201_a01.txt: SELECT * FROM DUAL WHERE 1=2  
#data/20160201_b01.txt: SELECT * FROM DUAL WHERE 1=2  
while read v_line  
do  
   [[ -z $(echo $v_line | grep -v '^ *#') ]] && continue  
   read <&8  
   (  
     v_fname=$(echo "$v_line"|awk -F: '{print $1}')  
     v_sqlst=$(echo "$v_line"|awk -F: '{print $2}')  
     echo "$v_sqlst"  
     echo "$v_fname"  
     sh exp.sh "$v_sqlst" $v_fname  
     echo 'z' >&8  
   ) &  
done<cfg.cfg  
wait  
exec 8>&-  
