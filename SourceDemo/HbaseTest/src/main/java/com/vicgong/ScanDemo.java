package com.vicgong;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class ScanDemo {
    public static void main(String[] args) throws IOException {
        String tname = "blog";
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.rootdir", "hdfs://192.168.1.111:9000/hbase");
        conf.set("hbase.zookeeper.quorum", "192.168.1.111");
        conf.set("hbase.zookeeper.property.clientPort", "2181");
        conf.set("zookeeper.znode.parent", "/hbase");
        Connection conn = ConnectionFactory.createConnection(conf);
        TableName tableName = TableName.valueOf(tname);
        Table table = conn.getTable(tableName);
        Scan scan = new Scan();
//        如果读取所有的记录则不需要指定起始和结束的行键
//        scan.setStartRow(Bytes.toBytes("blog1"));
//        scan.setStopRow(Bytes.toBytes("blog5"));
        scan.addColumn(Bytes.toBytes("article"),Bytes.toBytes("title"));
        scan.addColumn(Bytes.toBytes("article"),Bytes.toBytes("tag"));
        scan.addColumn(Bytes.toBytes("author"),Bytes.toBytes("name"));
        scan.setCaching(100);
        ResultScanner results = table.getScanner(scan);
        for(Result result : results){
            while(result.advance()){
                Cell cell = result.current();
                String rowkey = Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
                String family =  Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength());
                String qualifier =  Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
                String value = Bytes.toString(cell.getValueArray(),cell.getValueOffset(),cell.getValueLength());
                //String value = Bytes.toString(CellUtil.cloneValue(cell));
                System.out.println("Row => " + rowkey + " Column => " + family + ":" + qualifier + " Value => " + value);
            }
        }
        results.close();
        table.close();
        conn.close();
    }
}

