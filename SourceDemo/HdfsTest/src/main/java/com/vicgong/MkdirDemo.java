package com.vicgong;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;

public class MkdirDemo {
    public static void main(String[] args) throws IOException {
        String myhome = "/user/gmd";
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS","hdfs://192.168.1.111:9000");
        System.setProperty("HADOOP_USER_NAME","bigdata");
        FileSystem fs = FileSystem.get(conf);
        Path path = new Path(myhome);
        if(!fs.exists(path)){
            System.out.println(fs.mkdirs(path) ? "success" : "failure");
        } else {
            System.out.println(myhome + " exists...");
        }
        fs.close();
    }
}
