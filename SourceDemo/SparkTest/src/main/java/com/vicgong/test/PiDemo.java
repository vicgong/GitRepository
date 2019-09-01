package com.vicgong.test;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.ArrayList;
import java.util.List;

public class PiDemo {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setMaster("local").setAppName("PiDemo");
        JavaSparkContext jsc = new JavaSparkContext(conf);
        //指定计算的partition数量，即并行度
        int slices = 2;
        if (args.length > 0) {
            slices = Integer.parseInt(args[0]);
        }
        //设置投掷的个数
        int n = (int)Math.min(100000L * slices, Integer.MAX_VALUE);
        List<Integer> list = new ArrayList<Integer>(n);
        for (int i = 1;i < n;i++) {
            list.add(i);
        }
        JavaRDD<Integer> rdd = jsc.parallelize(list, slices);
        //获得随机的点，根据勾股弦定理 x^2 + y^2 = h^2
        //如果斜边的长度小于半径，则表示点在圆里面
        JavaRDD<Integer> mapRdd = rdd.map(v1 -> {
            double x = Math.random() * 2 - 1;
            double y = Math.random() * 2 - 1;
            return (x * x + y * y <= 1) ? 1 : 0;
        });
        int count = mapRdd.reduce((v1, v2) -> v1 + v2);
        System.out.println("Pi is roughly " + 4.0 * count / n);
        jsc.stop();
    }
}
