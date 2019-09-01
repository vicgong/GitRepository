package com.vicgong.test;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;

public class RddTransformDemo1 {
    public static void main(String[] args) {
        String masterUrl = "local[1]";
        // Create a Java Spark Context.
        SparkConf conf = new SparkConf().setMaster(masterUrl).setAppName("Example2");
        JavaSparkContext jsc = new JavaSparkContext(conf);
        //设置日志输出级别为WARN, 方便查看调试的结果输出
        jsc.setLogLevel("WARN");
        List<Integer> result = new ArrayList();
        for(int i = 100; i <= 1000; i++) result.add(i);
        JavaRDD<Integer> input = jsc.parallelize(result, 7);

        /**
         * 按照要求输出结果
         * • 打印input中前5个数据
         * • 输出input中所有元素和
         * • 输出input中所有元素的平均值
         * • 统计inpu中偶数的个数，并打印前5个
         */

        // Todo add code to here
        System.out.println("head 5 elements are:" + input.take(5));
        System.out.println("sum is:" + input.reduce((a,b) -> a + b));
        System.out.println("avg is:" + input.reduce((a,b) -> a + b) / input.count());
        Tuple2<Integer,Integer> pair = input.mapToPair(x -> new Tuple2<Integer,Integer>(x, 1))
                .reduce((x,y) -> new Tuple2<>(x._1 + x._2, y._1 + y._2));
        System.out.println("Mr avg is:" + pair._1 / pair._2);
        System.out.println("count of even number is:" + input.filter(x -> x % 2 == 0).count());
        System.out.println("head 5 of even number is:" + input.filter(x -> x % 2 == 0).take(5));
        jsc.stop();
    }
}
