package com.vicgong.test;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.glassfish.jersey.server.Broadcaster;
import scala.Tuple2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IpDemo {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setMaster("local").setAppName("VariableDemo");
        JavaSparkContext jsc = new JavaSparkContext(conf);
//        List<Tuple2<Integer,String>> list = Arrays.asList(new Tuple2<>(001,"200,100,9,01"));
//        // logRDD contains (userId -> ip), type is [Int, String]
//        JavaPairRDD<Integer, String> logRDD = jsc.parallelizePairs(list);
//        // ips is memory hashmap, contains (ip -> provoince)
//        Map<String, String> ips = new HashMap<>();
//        ips.put("200,100,9,01","beijing");
//        JavaPairRDD<Integer, String> resultRDD = logRDD.mapToPair(pair -> {
//            String province = ips.get(pair._2());
//            return new Tuple2<>(pair._1(), province );
//        });

        List<Tuple2<Integer,String>> list = Arrays.asList(new Tuple2<>(001,"200,100,9,01"));
        // logRDD contains (userId -> ip), type is [Int, String]
        JavaPairRDD<Integer, String> logRDD = jsc.parallelizePairs(list);
        // ips is memory hashmap, contains (ip -> provoince)
        Map<String, String> ips = new HashMap<>();
        ips.put("200,100,9,01","beijing");
        Broadcast<Map<String, String>> bips = jsc.broadcast(ips);
        JavaPairRDD<Integer, String> resultRDD = logRDD.mapToPair(pair -> {
            String province = bips.value().get(pair._2());
            return new Tuple2<>(pair._1(), province );
        });
        resultRDD.foreach(p -> System.out.println("userId: " + p._1 + ",province: " + p._2));

    }
}
