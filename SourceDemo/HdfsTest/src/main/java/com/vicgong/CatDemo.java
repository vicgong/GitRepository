package com.vicgong;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class CatDemo {
    public static void main(String[] args) {
        //使用uri1必须使用hadoop jar命令执行,使用默认HDFS的URI地址
        String uri1 = "/hdfs/data/a.log";

        //使用uri2可直接java -cp运行
        String uri2 = "hdfs://192.168.1.111:9000/hdfs/data/a.log";

        //加载HDFS的URI下的core-site.xml
        Configuration conf = new Configuration();
        FileSystem fs = null;
        InputStream in = null;
        try {
            fs = FileSystem.get(URI.create(uri2),conf); //返回文件系统句柄
            in = fs.open(new Path(uri2));
            IOUtils.copyBytes(in,System.out,4096,false);
        } catch (IOException e) {
            e.printStackTrace();
            IOUtils.closeStream(in);
        } finally {
            try {
                if(fs != null) fs.close(); //关闭句柄
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
