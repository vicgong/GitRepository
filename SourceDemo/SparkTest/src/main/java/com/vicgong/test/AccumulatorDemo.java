package com.vicgong.test;

import org.apache.spark.Accumulator;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.Arrays;
import java.util.List;

public class AccumulatorDemo {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setMaster("local").setAppName("VariableDemo");
        JavaSparkContext jsc = new JavaSparkContext(conf);
//        List<Tuple2<String,Integer>> list = Arrays.asList(new Tuple2<>("url",500));
//        // accessLogRDD contains (url, code), type is RDD[(String, Int)]
//        JavaPairRDD<String, Integer> accessLogRDD = jsc.parallelizePairs(list);
//        JavaPairRDD<String, Integer> resultRDD = accessLogRDD
//                .filter(p -> p._2 % 100 == 0)
//                .reduceByKey((m,n) -> m + n);
//        long total = accessLogRDD.count();
//        resultRDD.foreach(p -> System.out.println("URL: " + p._1 + ",Percent: " + p._2/total));

        List<Tuple2<String,Integer>> list = Arrays.asList(new Tuple2<>("url",500));
        // accessLogRDD contains (url, code), type is RDD[(String, Int)]
        JavaPairRDD<String, Integer> accessLogRDD = jsc.parallelizePairs(list);
        Accumulator<Integer> accum = jsc.accumulator(0);
        List<Tuple2<String,Integer>> result = accessLogRDD
                .filter(p -> {accum.add(1); return p._2 / 100 == 5;})
                .mapToPair(s -> new Tuple2<>(s._1(),1))
                .reduceByKey((m,n) -> m + n)
                .collect();
        int total = accum.value();
        for(Tuple2<String,Integer> p : result){
            System.out.println("URL: " + p._1 + ",Percent: " + p._2/total*100 + "%");
        }
    }
}
