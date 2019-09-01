package com.vicgong.test;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;
import scala.Tuple4;

import java.util.Arrays;

public class WalMartDemo2 {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setMaster("local").setAppName("WalMartDemo");
        JavaSparkContext jsc = new JavaSparkContext(conf);
        /**
         * 沃尔玛全国各个分店每 天的流水，分别表示
         * 分店ID（ id），交易量（x），交易额（ y）和利润（z），类型均为整数
         */
        String[] input = {
                "1,23,5600,5",
                "2,30,5800,7",
                "1,27,5000,10",
                "3,24,6900,5",
                "2,45,5800,7",
                "2,28,5800,7"
        };
        /**
         * 用RDD的transformation函数实现下列功能:
         * SELECT id, SUM(x), MAX(y), MIN(z), AVERAGE(x) FROM T GROUP BY id;
         */
        jsc.parallelize(Arrays.asList(input))
                .map(s -> s.split(","))
                .filter(arr -> arr.length == 4)
                .mapToPair(arr -> new Tuple2<>(arr[0],new Tuple4<>(
                        Integer.parseInt(arr[1]),
                        Integer.parseInt(arr[2]),
                        Integer.parseInt(arr[3]),
                        1)))
                .reduceByKey((m,n) -> new Tuple4<>(
                                m._1() + n._1(),
                                Math.max(m._2() , n._2()),
                                Math.min(m._3() , n._3()),
                                m._4() + n._4()))
                .mapValues(v -> new Tuple4<>(v._1(),v._2(),v._3(),v._1() * 1.0 / v._4()))
                .sortByKey().foreach(x -> System.out.println("ID " + x._1
                                + ", sum(x)=" + x._2._1()
                                + ", max(y)=" + x._2._2()
                                + ", min(z)=" + x._2._3()
                                + ", avg(x)=" + x._2._4())
                );
        jsc.stop();
    }
}
