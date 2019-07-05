package com.vicgong;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.net.URI;

public class CopyDemo {
    public static void main(String[] args) throws IOException, InterruptedException {
        Configuration conf = new Configuration();
        /**
         * 打成jar执行时使用了hdfs://IP:port的schema
         * 而在生成的最终jar包中，无法找到这个schema的实现。所以就抛出了
         * java.io.IOException: No FileSystem for scheme: hdfs
         * 解决方案是，在设置hadoop的配置的时候，显示设置这个类：
         * org.apache.hadoop.hdfs.DistributedFileSystem
         */
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        String uri = "hdfs://192.168.1.111:9000/";
        FileSystem fs = FileSystem.get(URI.create(uri),conf,"bigdata");
        Path src = new Path("E:\\GitRepository\\readme.txt");
        Path dst = new Path("/hdfs/data/readme.txt");
        fs.copyFromLocalFile(src,dst);
        fs.close();
    }
}
