package com.vicgong.test;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class RddTransformDemo2 {
    public static void main(String[] args) {
        String masterUrl = "local[1]";

        // Create a Java Spark Context.
        SparkConf conf = new SparkConf().setMaster(masterUrl).setAppName("Example2");
        JavaSparkContext jsc = new JavaSparkContext(conf);
        List<Tuple2<String, Integer>> data = Arrays.asList(
                new Tuple2("coffee", 1),
                new Tuple2("coffee", 3),
                new Tuple2("panda", 4),
                new Tuple2("coffee", 5),
                new Tuple2("street", 2),
                new Tuple2("panda", 5)
        );
        JavaPairRDD<String, Integer> input = jsc.parallelizePairs(data);

        /**
         * 计算相同key对应的所有value的平均值，并把结果保存到目录/tmp/output下
         */

        // Todo add code to here
        JavaPairRDD<String, Double> result = input.mapValues(v -> new Tuple2<>(v,1))
                .reduceByKey((t,v) -> new Tuple2<Integer,Integer>(t._1 + v._1,t._2 + v._2))
                .mapToPair(t -> new Tuple2<String,Double>(t._1,t._2._1*1.0 / t._2._2));
        result.saveAsTextFile("file:///tmp/output");
        System.out.println("output: \n" + result.collect());
        jsc.stop();
    }
}

