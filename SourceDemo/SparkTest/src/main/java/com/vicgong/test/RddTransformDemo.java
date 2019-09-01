package com.vicgong.test;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import java.util.Arrays;
import java.util.List;

public class RddTransformDemo {
    public static void main(String[] args) {
        String masterUrl = "local[1]";
        // Create a Java Spark Context.
        SparkConf conf = new SparkConf().setMaster(masterUrl).setAppName("Example1");
        JavaSparkContext jsc = new JavaSparkContext(conf);
        //设置日志输出级别为WARN, 方便查看调试的结果输出
        jsc.setLogLevel("WARN");
        List<Integer> data = Arrays.asList(1, 2, 3, 4, 5, 6);
        JavaRDD<Integer> rdd1 = jsc.parallelize(data, 3);
        List<Integer> data2 = Arrays.asList(7, 8, 9, 10, 11);
        JavaRDD<Integer> rdd2 = jsc.parallelize(data,22);
        List<Integer> data3 = Arrays.asList(12, 13, 14, 15, 16, 17, 18, 19, 20, 1);
        JavaRDD<Integer> rdd3 = jsc.parallelize(data3, 3);

        /**
         * 实现以下功能
         * – 使用union连接rdd1和rdd2，生成rdd4
         * – 使用glom打印rdd4的各个partition
         * – 使用coalesce将rdd4的分区数改为3，并生成rdd5
         * – 使用repartition将rdd4的分区数改为10，并生成rdd6
         * – 使用glom分别打印rdd5和rdd6中的partition元素均匀性
         */

        // Todo add code to here
        JavaRDD<Integer> rdd4 = rdd1.union(rdd3);
        System.out.println("Rdd4 has partition : \n" + rdd4.glom().collect());
        JavaRDD<Integer> rdd5 = rdd1.union(rdd3).coalesce(3);
        System.out.println("Rdd5 has partition : \n" + rdd5.glom().collect());
        JavaRDD<Integer> rdd6 = rdd1.union(rdd3).repartition(10);
        System.out.println("Rdd5 has partition : \n" + rdd6.glom().collect());
        jsc.stop();
    }
}
