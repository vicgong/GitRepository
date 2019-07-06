package com.vicgong;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class CreateDemo {
    public static void main(String[] args) throws IOException {
        String tname = "blog";
        String cf1 = "article";
        String cf2 = "author";
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.rootdir", "hdfs://192.168.1.111:9000/hbase");
        conf.set("hbase.zookeeper.quorum", "192.168.1.111");
        conf.set("hbase.zookeeper.property.clientPort", "2181");
        conf.set("zookeeper.znode.parent", "/hbase");
        Connection conn = ConnectionFactory.createConnection(conf);
        Admin admin = conn.getAdmin();
        TableName tablename = TableName.valueOf(tname);
        if(!admin.tableExists(tablename)){
            HTableDescriptor hTableDescriptor = new HTableDescriptor(tablename);
            HColumnDescriptor hColumnDescriptor1 = new HColumnDescriptor(Bytes.toBytes(cf1));
            HColumnDescriptor hColumnDescriptor2 = new HColumnDescriptor(Bytes.toBytes(cf2));
            hTableDescriptor.addFamily(hColumnDescriptor1);
            hTableDescriptor.addFamily(hColumnDescriptor2);
            admin.createTable(hTableDescriptor);
            TableName[] tableNames = admin.listTableNames(tname);
            if(tableNames.length == 1 && Bytes.equals(Bytes.toBytes(tname),tableNames[0].getName())){
                System.out.println(tname + " create successful");
            } else {
                throw new IOException(tname + "failure to create");
            }
        }
        admin.close();
        conn.close();
    }
}
