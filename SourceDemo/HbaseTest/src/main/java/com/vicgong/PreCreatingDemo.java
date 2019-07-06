package com.vicgong;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableExistsException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.math.BigInteger;

public class PreCreatingDemo {

    public static boolean createTable(Admin admin, HTableDescriptor table, byte[][] splits)
            throws IOException {
        try {
            admin.createTable(table, splits);
            return true;
        } catch (TableExistsException e) {
            // the table already exists...
            System.out.println("table " + table.getNameAsString() + " already exists");
            return false;
        }
    }

    public static byte[][] getHexSplits(String startKey, String endKey, int numRegions) {
        byte[][] splits = new byte[numRegions-1][];
        BigInteger lowestKey = new BigInteger(startKey, 16);
        BigInteger highestKey = new BigInteger(endKey, 16);
        BigInteger range = highestKey.subtract(lowestKey);
        BigInteger regionIncrement = range.divide(BigInteger.valueOf(numRegions));
        lowestKey = lowestKey.add(regionIncrement);
        for(int i=0; i < numRegions-1;i++) {
            BigInteger key = lowestKey.add(regionIncrement.multiply(BigInteger.valueOf(i)));
            byte[] b = String.format("%016x", key).getBytes();
            splits[i] = b;
        }
        return splits;
    }

    public static void main(String[] args) throws IOException {
        Configuration conf = EmployeeDemo.getConfiguration();
        conf.set("hbase.rootdir", "hdfs://192.168.1.111:9000/hbase");
        conf.set("hbase.zookeeper.quorum", "192.168.1.111");
        conf.set("hbase.zookeeper.property.clientPort", "2181");
        conf.set("zookeeper.znode.parent", "/hbase");
        Connection connection = ConnectionFactory.createConnection(conf);
        Admin admin = connection.getAdmin();
        //创建表同时预分区10
        HTableDescriptor hTableDescriptor = new HTableDescriptor(TableName.valueOf("employee1"));
        HColumnDescriptor hColumnDescriptor = new HColumnDescriptor(Bytes.toBytes("baseinfo"));
        hTableDescriptor.addFamily(hColumnDescriptor);
        byte[][] splits = getHexSplits("e001","e999",10);
        createTable(admin,hTableDescriptor,splits);
    }
}
