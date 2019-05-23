#!/bin/sh 
# Filename: showrpm.sh
# EXAMPLE: showrpm /cdrom/RedHat/RPMS/*.rpm
# EXAMPLE: showrpm openssh.rpm w3m.rpm webgrep.rpm
for rpmpackage in $*; do
# $* 包含了所有输入的命令行参数值
if [ -r "$rpmpackage" ];then
echo "=============== $rpmpackage =============="   
rpm -qi -p $rpmpackage
else
echo "ERROR: cannot read file $rpmpackage"
fi
done

