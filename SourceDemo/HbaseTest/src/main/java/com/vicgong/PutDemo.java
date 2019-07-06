package com.vicgong;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class PutDemo {
    public static void main(String[] args) throws IOException {
        String tname = "blog";
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.rootdir", "hdfs://192.168.1.111:9000/hbase");
        conf.set("hbase.zookeeper.quorum", "192.168.1.111");
        conf.set("hbase.zookeeper.property.clientPort", "2181");
        conf.set("zookeeper.znode.parent", "/hbase");
        Connection conn = ConnectionFactory.createConnection(conf);
        TableName tablename = TableName.valueOf(tname);
        Table table = conn.getTable(tablename);
        Put put = new Put(Bytes.toBytes("blog4"));
        put.addColumn(Bytes.toBytes("article"),Bytes.toBytes("title"),Bytes.toBytes("hbase"));
        put.addColumn(Bytes.toBytes("article"),Bytes.toBytes("content"),Bytes.toBytes("hbase Java API"));
        put.addColumn(Bytes.toBytes("article"),Bytes.toBytes("tag"),Bytes.toBytes("storage"));
        put.addColumn(Bytes.toBytes("author"),Bytes.toBytes("name"),Bytes.toBytes("vicgong"));
        put.addColumn(Bytes.toBytes("author"),Bytes.toBytes("gender"),Bytes.toBytes("male"));
        put.addColumn(Bytes.toBytes("author"),Bytes.toBytes("age"),Bytes.toBytes(27));
        table.put(put);
        table.close();
        conn.close();
    }
}
