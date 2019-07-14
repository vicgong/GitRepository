package com.vicgong.MrAPI;

import org.apache.hadoop.conf.Configuration;

public class ConfDemo {
    public static void main(String[] args) {
        Configuration conf = new Configuration();

        //添加资源文件configuration-1.xml到conf
        conf.addResource("configuration-1.xml");
        System.out.println(conf.get("color"));
        System.out.println(conf.getInt("size",0));
        System.out.println(conf.get("weight"));
        System.out.println(conf.get("size-weight"));

        //添加资源文件configuration-2.xml到conf
        conf.addResource("configuration-2.xml");
        System.out.println(conf.getInt("size",0)); //size属性值：10被覆盖为12
        System.out.println(conf.get("weight")); //weight属性值无法被覆盖：heavy

        //如果Hadoop用户标识不同于客户机上的用户账号
        conf.set("HADOOP_USER_NAME", "bigdata");
        System.out.println(conf.get("HADOOP_USER_NAME"));
    }
}
