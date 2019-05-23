#!/bin/bash
#文件名：etherconnect.sh
#用途：连接以太网
#根据设置修改下面参数
######### PARAMETERS #########
IFACE=eth0
IP_ADDR=192.168.11.110
SUBNET_MASK=255.255.255.0
GW=192.168.11.2
HD_ADDR=00:0C:29:71:77:F6
# HD_ADDR  是可选的
##############################
if [ $UID -ne 0 ]; 
then
   echo "RUN AS ROOT"
   exit 1
fi
if [ -n $HD_ADDR ]; then 
   /sbin/ifconfig hw ether $HD_ADDR
   echo Spoofed MAC ADDRESS to $HD_ADDR
fi

/sbin/ifconfig $IFACE $IP_ADDR metmask $SUBNET_MASK
route add default gw $GW $IFACE
echo Successful configured $IFACE

