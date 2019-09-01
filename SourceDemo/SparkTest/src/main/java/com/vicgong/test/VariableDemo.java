package com.vicgong.test;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.Arrays;
import java.util.List;

public class VariableDemo {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setMaster("local").setAppName("VariableDemo");
        JavaSparkContext jsc = new JavaSparkContext(conf);
        List<Tuple2<String,Integer>> list = Arrays.asList(new Tuple2<>("user",20));
        JavaPairRDD<String, Integer> userRDD = jsc.parallelizePairs(list);
        int n = 20;
        JavaPairRDD<String, Integer> resultRDD = userRDD.filter(x -> x._2 == n);
    }
}
