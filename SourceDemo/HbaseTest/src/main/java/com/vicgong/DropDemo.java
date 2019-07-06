package com.vicgong;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import java.io.IOException;

public class DropDemo {
    public static void main(String[] args) throws IOException {
        String tname = "blog";
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.rootdir", "hdfs://192.168.1.111:9000/hbase");
        conf.set("hbase.zookeeper.quorum", "192.168.1.111");
        conf.set("hbase.zookeeper.property.clientPort", "2181");
        conf.set("zookeeper.znode.parent", "/hbase");
        Connection conn = ConnectionFactory.createConnection(conf);
        Admin admin = conn.getAdmin();
        TableName tablename = TableName.valueOf(tname);
        if (admin.tableExists(tablename)) {
            if (!admin.isTableDisabled(tablename)) {
                admin.disableTable(tablename);
            }
            admin.deleteTable(TableName.valueOf(tname));
            System.out.println(tname + " delete successful");
        }
        admin.close();
        conn.close();
    }
}
