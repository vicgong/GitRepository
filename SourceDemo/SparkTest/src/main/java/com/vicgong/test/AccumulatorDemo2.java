package com.vicgong.test;

import org.apache.spark.Accumulator;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class AccumulatorDemo2 {
    public static void main(String[] args) {
        if(args.length != 2){
            System.out.println("Usage : AccumulatorDemo2 [masterUrl] [inputPath]");
            System.exit(1);
        }
        String masterUrl = args[0];
        String inputPath = args[1];
        SparkConf conf = new SparkConf().setMaster(masterUrl).setAppName("AccumulatorDemo2");
        JavaSparkContext jsc = new JavaSparkContext(conf);
        jsc.setLogLevel("WARN");
        Accumulator<Integer> accum_404 = jsc.accumulator(0);
        Accumulator<Integer> accum_200 = jsc.accumulator(0);
        JavaRDD<String[]> input = jsc.textFile(inputPath).map(x -> x.split(",")).cache();
        /**
         * 每行数据如下：
         * 404,42,d.html，
         */
        input.foreach(arr -> {
                 if(arr[0].equals("404")) accum_404.add(1);
                 if(arr[0].equals("200")) accum_200.add(1);
             });
        long count = input.count();
        System.out.println("总记录数:" + count
                + "， 404出现次数:" + accum_404.value()
                + "， 200出现次数:" + accum_200.value()
        );
        /**
         * 输出：
         * 总记录数:1000， 404出现次数:322， 200出现次数:349
         */
    }
}
