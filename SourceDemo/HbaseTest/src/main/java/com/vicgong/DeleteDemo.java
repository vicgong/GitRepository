package com.vicgong;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class DeleteDemo {
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
        Delete delete = new Delete(Bytes.toBytes("blog3"));
        delete.addColumn(Bytes.toBytes("article"),Bytes.toBytes("tag"));
        table.delete(delete);
        System.out.println("delete successful...");
        table.close();
        conn.close();
    }
}
