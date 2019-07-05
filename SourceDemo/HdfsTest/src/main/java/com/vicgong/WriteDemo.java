package com.vicgong;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;

public class WriteDemo {
    public static void main(String[] args) throws IOException {
        Configuration conf = new Configuration();
        byte[] buff = "hello hdfs".getBytes();
        FileSystem fs = FileSystem.get(conf);
        FSDataOutputStream out = fs.create(new Path("/hdfs/data/test.txt"));
        out.write(buff,0,buff.length);
        out.close();
        fs.close();
    }
}
