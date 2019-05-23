#!/bin/bash
# �ļ�����batch_mkctl.sh
# ��;����������sqlldr�����ļ�,����infileѡ��

USAGE="USAGE:`basename $0` user/passwd@domain tabconf"

#�ж������в��������Ƿ�Ϊ3
if [ $#  -ne 2 ];then
   echo $USAGE
   exit 1
fi

#�����в�����ֵ
userid=$1
tabconf=$2

#ѭ������ctl�ű�

for tablename in `cat $tabconf` ; do
#��ȡ�ñ��ctl���
#number��integer�����trim
#char��varchar��varchar2 ���ȴ���255ʹ��ԭ���ĳ���
#date ʹ�� date 'yyyy-mm-dd hh24:mi:ss'ת��
#���������ʱ�ļ���ɾ��
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

#������ڿ����ļ���ɾ��
[ -e $tablename.ctl ] && rm -f $tablename.ctl
cat $tablename.temp | \
awk 'BEGIN{
           print "OPTIONS(BINDSIZE=16777216,READSIZE=16777216,ERRORS=8,ROWS=80000) "
           print "LOAD DATA "
           print "TRUNCATE" 
           print "INTO TABLE '$tablename'"
           # | �ָ���
           #print "fields terminated by '\''|'\''" 
           # @|@ �ָ���
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
