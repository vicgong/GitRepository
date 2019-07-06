package com.vicgong;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Strings;

import java.io.IOException;

public class GetDemo {
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
        Get get = new Get(Bytes.toBytes("blog2"));
        get.addColumn(Bytes.toBytes("author"),Bytes.toBytes("name"));
        get.addColumn(Bytes.toBytes("author"),Bytes.toBytes("age"));
        Result rs = table.get(get);
        while(rs.advance()){
            Cell cell = rs.current();
            String rowkey = Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
            String family =  Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength());
            String qualifier =  Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
            String value = Bytes.toString(cell.getValueArray(),cell.getValueOffset(),cell.getValueLength());
            //String value = Bytes.toString(CellUtil.cloneValue(cell));
            System.out.println("Row => " + rowkey + " Column => " + family + ":" + qualifier + " Value => " + value);
        }
        table.close();
        conn.close();
    }
}
