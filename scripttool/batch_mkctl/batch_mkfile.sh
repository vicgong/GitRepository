#!/bin/bash
##########################################################
# 文件 ：batch_mkfile.sh                                 #
# 用途 ：批量创建sqlldr控制文件以及调用sqlldr的shell脚本 #
# 参数1: 连接Oracle数据库的"用户/密码@实例"              #
# 参数2: 表名称与数据文件的映射文件                      #
# 参数3: 指定数据文件路径的脚本,选项sh|ctl               #
# 例如1: 生成CTL以及SH脚本在CTL中指定数据文件路径：	 #
# sh batch_mkctl.sh scott/tiger@orcl tabconf ctl         #
# 例如2: 生成CTL以及SH脚本在SH中指定数据文件路径：	 #
# sh batch_mkctl.sh scott/tiger@orcl tabconf sh          #
##########################################################

# STEP01 初始话

# 定义USAGE变量
USAGE="USAGE: \nsh "`basename $0`' user/passwd@domain tabconf sh|ctl'

# 控制文件目录默认值
BASEDIR=/awp/shell

# 数据文件路径默认值
DATADIR=/awp/workpath

# 数据文件的分隔符
# DELIMITER='|'
DELIMITER='@|@'

# 执行日期
VDATE=$(date +"%Y%m%d")

# 创建目录保存生产的文件
mkdir -p ./$VDATE
rm -f ./$VDATE/*

# 日志文件名称
LOGFILE=mkfile_$VDATE.log

# 定义日志函数
log()
{
	echo "[`date +%Y%m%d\" \"%X`] [$*]" >> $LOGFILE
}

# STEP02 判断命令行参数个数是否为三个
if [ $#  -ne 3 ]; then
   echo -e $USAGE
   exit 1
fi

# STEP03 命令行参数赋值
# 数据库SID用户密码
SIDFULL=$1

# 映射文件名称
MAPFILE=$2

# 生成模式
INFILE=$3

# STEP04 创建获取字段结构视图 V_TAB_COLTYPE:
# /****************
#  * number|integer		添加trim处理,防止空格带来数值型导入的异常
#  * char|varchar|varchar2	数据类型长度大于255时使用字段的长度进行扩展
#  * 				sqlldr默认的字符串最大长度为255,多于255就会舍弃
#  * 日期或时间戳类型转换	根据提供的数据文件中的定义来进行修改转换函数
#  * date 			默认使用 to_date(:filed,'YYYY-MM-DD HH24:MI:SS')
#  * timestamp(3)		默认使用 to_timestamp(:field,'YYYY-MM-DD HH24:MI:SS:FF3')
#  * timestamp(6)		默认使用 to_timestamp(:field,'YYYY-MM-DD HH24:MI:SS:FF6')
#  * timestamp(9)		默认使用 to_timestamp(:field,'YYYY-MM-DD HH24:MI:SS:FF9')
# ***********************************************************************************/
# CREATE OR REPLACE VIEW V_TAB_COLTYPE
# AS 
# SELECT A.TABLE_NAME
#       ,A.COLUMN_NAME
#       ,A.COLUMN_ID
#       ,CASE WHEN A.DATA_TYPE IN ('NUMBER','INTEGER')
#               THEN '"TRIM(:'||A.COLUMN_NAME||')"'
#             WHEN A.DATA_TYPE IN ('CHAR','VARCHAR','VARCHAR2') AND A.DATA_LENGTH >= 255
#               THEN 'CHAR('||A.DATA_LENGTH||')'
#             WHEN A.DATA_TYPE = 'DATE'
#               THEN '"TO_DATE(:'||A.COLUMN_NAME||',''YYYY-MM-DD HH24:MI:SS'')"'
#             WHEN A.DATA_TYPE = 'TIMESTAMP(3)' 
#                THEN '"TO_TIMESTAMP(:'||A.COLUMN_NAME||',''YYYY-MM-DD HH24:MI:SS:FF3'')"'
#             WHEN A.DATA_TYPE = 'TIMESTAMP(6)'
#                THEN '"TO_TIMESTAMP(:'||A.COLUMN_NAME||',''YYYY-MM-DD HH24:MI:SS:FF6'')"'
#             WHEN A.DATA_TYPE = 'TIMESTAMP(9)'
#                THEN '"TO_TIMESTAMP(:'||A.COLUMN_NAME||',''YYYY-MM-DD HH24:MI:SS:FF9'')"'
#             ELSE ' ' END CTL_TYPE
#   FROM DBA_TAB_COLUMNS A;

# 在数据库创建该视图
sqlplus -S $SIDFULL <<END >>$LOGFILE 2>&1
CREATE OR REPLACE VIEW V_TAB_COLTYPE
AS 
SELECT A.OWNER
	  ,A.TABLE_NAME
      ,A.COLUMN_NAME
      ,A.COLUMN_ID
      ,CASE WHEN A.DATA_TYPE IN ('NUMBER','INTEGER')
              THEN '"TRIM(:'||A.COLUMN_NAME||')"'
            WHEN A.DATA_TYPE IN ('CHAR','VARCHAR','VARCHAR2') AND A.DATA_LENGTH >= 255
              THEN 'CHAR('||A.DATA_LENGTH||')'
            WHEN A.DATA_TYPE = 'DATE'
              THEN '"TO_DATE(:'||A.COLUMN_NAME||',''YYYY-MM-DD HH24:MI:SS'')"'
            WHEN A.DATA_TYPE = 'TIMESTAMP(3)' 
               THEN '"TO_TIMESTAMP(:'||A.COLUMN_NAME||',''YYYY-MM-DD HH24:MI:SS:FF3'')"'
            WHEN A.DATA_TYPE = 'TIMESTAMP(6)'
               THEN '"TO_TIMESTAMP(:'||A.COLUMN_NAME||',''YYYY-MM-DD HH24:MI:SS:FF6'')"'
            WHEN A.DATA_TYPE = 'TIMESTAMP(9)'
               THEN '"TO_TIMESTAMP(:'||A.COLUMN_NAME||',''YYYY-MM-DD HH24:MI:SS:FF9'')"'
            ELSE ' ' END CTL_TYPE
  FROM DBA_TAB_COLUMNS A;
END

# 判断视图是否创建成功
CON=`sqlplus -s $SIDFULL <<END
set heading off
set feedback off
set pagesize 0 
set verify off 
set echo off
SELECT COUNT(1) FROM DBA_VIEWS WHERE OWNER = 'ODS' AND VIEW_NAME = 'V_TAB_COLTYPE';
exit
END
`
if [ $CON -eq 1 ];then
	log "[`date +%Y%m%d\" \"%X`] VIEW V_TAB_COLTYPE CREATE SUCCESSFUL!"
	echo "[`date +%Y%m%d\" \"%X`] VIEW V_TAB_COLTYPE CREATE SUCCESSFUL!"
else
	echo "VIEW V_TAB_COLTYPE CREATE FAILOVER, PLEASE CHECK IN"
	exit 1
fi

# STEP05 循环创建ctl脚本以及shell脚本
## 说明：tabconf文件中记录了"表名"和"数据文件名"的映射关系,通过逗号分隔
for read in `cat $MAPFILE` ; do

## 说明：提取read中的值:表名,数据文件名
TABNAME=`echo $read | awk -F, '{print $1}'`
DATAFILE=`echo $read | awk -F, '{print $2}'`

# 判断shell文件是否存在,如果存在, 那么删除该shell文件
#[ -e ${TABNAME}_script.sh ] && rm -f ${TABNAME}_script.sh

## 说明：生成shell脚本
SCRIPTNAME=./${VDATE}/${TABNAME}_script.sh

if [[ $INFILE == "sh" ]]; then
	echo "sqlldr $SIDFULL control=${BASEDIR}/${TABNAME}.ctl, log=${BASEDIR}/log/${TABNAME}.log, bad=${BASEDIR}/bad/${TABNAME}.bad, data=${DATADIR}/${DATAFILE}" > $SCRIPTNAME
	echo >> $SCRIPTNAME
	echo " exit; " >> $SCRIPTNAME
elif [[ $INFILE == "ctl" ]]; then
	echo "sqlldr $SIDFULL control=${BASEDIR}/${TABNAME}.ctl, log=${BASEDIR}/log/${TABNAME}.log, bad=${BASEDIR}/bad/${TABNAME}.bad" > $SCRIPTNAME
	echo >> $SCRIPTNAME
	echo " exit; " >> $SCRIPTNAME
else 
	echo "第三个参数,可选项为：sh或ctl"
	exit 1
fi

log "[`date +%Y%m%d\" \"%X`] $SCRIPTNAME make successful!"
echo "[`date +%Y%m%d\" \"%X`] $SCRIPTNAME make successful!"


# 临时文件用于存储ctl文件的字段序列,存在临时文件已存在那么删除该临时文件
[ -e $TABNAME.temp ] && rm -f $TABNAME.temp

# STEP06 写入字段序列
log "[`date +%Y%m%d\" \"%X`] START FILED LIST!"
echo "spool ${TABNAME}.temp;
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
      select column_name
			,ctl_type 
        from v_tab_coltype 
	   where owner = upper('$(echo $SIDFULL | cut -d / -f 1)')
	     and table_name = upper('$TABNAME')
	   order by table_name,column_id;
      spool off; " | sqlplus -S $SIDFULL >>$LOGFILE 2>&1

log "[`date +%Y%m%d\" \"%X`] FINISHED FILED LIST!"
#echo "[`date +%Y%m%d\" \"%X`] FINISHED FILED LIST!"

# 判断控制文件是否存在,如果存在, 那么删除该控制文件
#[ -e $TABNAME.ctl ] && rm -f $TABNAME.ctl

# STEP07 写入控制文件的头部代码段
cat $TABNAME.temp | \
awk 'BEGIN{
           print "OPTIONS(BINDSIZE=16777216,READSIZE=16777216,ERRORS=8,ROWS=80000) "
           print "LOAD DATA "
		   print "INFILE '\'''$DATADIR'/'$DATAFILE''\'' "
           print "TRUNCATE" 
           print "INTO TABLE '$TABNAME'"
           print "FIELDS TERMINATED BY '\'''$DELIMITER''\''"  
		   print "TRAILING NULLCOLS"
           print "("
         } 
	     { print $1,$2,","} ' | \
sed '$s/,//' | sed '$a )' >> ./${VDATE}/$TABNAME.ctl
# 替换最后一行的","为空, 同时在最后一行打印")"

# 如果数据文件的路径在sh中指定的话, ctl中则删除第三行
if [[ $INFILE == "sh" ]]; then
sed -i '3d' ./${VDATE}/$TABNAME.ctl
fi

# 删除临时文件
[ -e $TABNAME.temp ] && rm -f $TABNAME.temp

log "[`date +%Y%m%d\" \"%X`] ./${VDATE}/$TABNAME.ctl make successful!"
echo "[`date +%Y%m%d\" \"%X`] ./${VDATE}/$TABNAME.ctl make successful!"

done
