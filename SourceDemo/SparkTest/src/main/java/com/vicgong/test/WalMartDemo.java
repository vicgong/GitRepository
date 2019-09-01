package com.vicgong.test;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class WalMartDemo {
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
        Map<String,Map<String,Integer>> result = jsc.parallelize(Arrays.asList(input))
                .filter(s -> s.split(",").length == 4)
                .mapToPair(s -> {
                    Map<String,Integer> map = new HashMap<>();
                    String[] arr = s.split(",");
                    String id = arr[0];
                    map.put("x",Integer.parseInt(arr[1]));
                    map.put("y",Integer.parseInt(arr[2]));
                    map.put("z",Integer.parseInt(arr[3]));
                    map.put("c",1);
                    return new Tuple2<>(id, map); })
                .reduceByKey((m,n) -> {
                    Map<String,Integer> map = new HashMap<>();
                    map.put("x",m.get("x") + n.get("x"));
                    map.put("y",Math.max(m.get("y") , n.get("y")));
                    map.put("z",Math.min(m.get("z") , n.get("z")));
                    map.put("c",m.get("c") + n.get("c"));
                    return map; })
                .collectAsMap();
        System.out.println("id\tSUM(x)\tMAX(y)\tMIN(z)\tAVERAGE(x)");
        for(String id : result.keySet()){
            int sumx = result.get(id).get("x");
            int maxy = result.get(id).get("y");
            int minz = result.get(id).get("z");
            int averagex = sumx / result.get(id).get("c");
            System.out.println(id + "\t"+ sumx + "\t"+ maxy + "\t"+ minz + "\t"+ averagex);
        }
        /**
         * 输出结果
         * id      SUM(x)  MAX(y)  MIN(z)  AVERAGE(x)
         * 2       103     5800    7       34
         * 1       50      5600    5       25
         * 3       24      6900    5       24
         */
        jsc.stop();
    }
}
