#!/bin/bash
# 文件名：batch_mkctl.sh
# 用途：批量创建sqlldr控制文件,不含infile选项

USAGE="USAGE:`basename $0` user/passwd@domain tabconf"

#判断命令行参数个数是否为3
if [ $#  -ne 2 ];then
   echo $USAGE
   exit 1
fi

#命令行参数赋值
userid=$1
tabconf=$2

#循环创建ctl脚本

for tablename in `cat $tabconf` ; do
#获取该表的ctl情况
#number、integer等添加trim
#char、varchar、varchar2 长度大于255使用原本的长度
#date 使用 date 'yyyy-mm-dd hh24:mi:ss'转化
#如果存在临时文件便删除
[ -e $tablename.temp ] && rm -f $tablename.temp
echo "spool $tablename.temp;
      set feedback off
      set heading off
      set termout off
      set pagesize 0
      set linesize 500
      set numwidth 20
      set verify off
      set echo off
      set trimspool on
      set trimout on
      SELECT COLUMN_NAME,CTL_TYPE FROM tabctl WHERE TABLE_NAME = UPPER('$tablename')
		 ORDER BY TABLE_NAME,COLUMN_ID;
     spool off; "|sqlplus -S $userid >>/dev/null

#如果存在控制文件便删除
[ -e $tablename.ctl ] && rm -f $tablename.ctl
cat $tablename.temp | \
awk 'BEGIN{
           print "OPTIONS(BINDSIZE=16777216,READSIZE=16777216,ERRORS=8,ROWS=80000) "
           print "LOAD DATA "
           print "TRUNCATE" 
           print "INTO TABLE '$tablename'"
           # | 分隔符
           #print "fields terminated by '\''|'\''" 
           # @|@ 分隔符
           print "fields terminated by '\''@|@'\''"  
           print "("
           }  
     {
        if($2~/DATE/)
        print $1 "  " $2 " '\''YYYY-MM-DD HH24:MI:SS'\'',"  
        else
        print $1 "  " $2 ","
     } ' | sed '$s/,//'|sed '$a )'>>${tablename}.ctl
[ -e $tablename.temp ] && rm -f $tablename.temp
done
