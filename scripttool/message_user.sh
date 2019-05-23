#!/bin/bash
# Filename：message_user.sh
# Function：用于向指定用户记录的终端发送消息的脚本

if [ $# -ne 1 ]; then
	echo "Usage: message_user.sh user < <(echo "输入内容")"
	exit 1
fi
user=$1
devices=`ls /dev/pts/* -l | awk '{print $3,$10}' | grep "$user" | awk '{print $2}'`
for dev in $devices ; do
cat /dev/stdin > $dev
done
