#!/bin/bash
# Filename: stat_aliveHost.sh
for ip in 192.168.0.{0..255}; do 
ping -w 10 -c 10 $ip &> /dev/nul;
if [ $? -eq 0 ] ;then 
echo $ip is alive
else
echo $ip is unreachable
fi
done

