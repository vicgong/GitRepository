package com.vicgong.test;

import org.apache.spark.Accumulator;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import scala.Tuple2;

import java.util.*;

public class HamletStatistic {

    public static List<String> splitWord(String lint){
        List<String> list = new ArrayList<>();
        String[] words = lint.replaceAll("[\t\\pP\\p{Punct}]"," ").trim().split(" ");
        for(String word : words){
            if(!word.isEmpty()){
                list.add(word);
            }
        }
        return list;
    }

    public static void test(){
        String string = "ss&*(,.~1如果@&(^-自己!!知道`什`么#是$苦%……Z，&那*()么一-=定——+告诉::;\\\"'/?.,><[]{}\\\\||别人什么是甜。";
        String string1 = "                  O, farewell, honest soldier:";
        System.out.println(string1.replaceAll("[\t\\pP\\p{Punct}]"," ").trim());
    }

    public static void main(String[] args) {
        String masterUrl = "local";
        String DATA_PATH = "file:///home/bigdata/tmp";
        SparkConf conf = new SparkConf().setMaster(masterUrl).setAppName("HamletStatistic");
        JavaSparkContext jsc = new JavaSparkContext(conf);
        jsc.setLogLevel("WARN");
        //step01 加载数据
        JavaRDD<String> hamletRdd = jsc.textFile(DATA_PATH + "/Hamlet.txt");
        JavaRDD<String> stopwordRdd = jsc.textFile(DATA_PATH + "/stopword.txt");
        //step02 将停止词设置为广播变量，定义累加器记录停止词出现次数
        Set<String> stopSet = new HashSet<>(stopwordRdd.collect());
        Broadcast<Set<String>> bstop = jsc.broadcast(stopSet);
        Accumulator<Integer> count = jsc.accumulator(0);
        Accumulator<Integer> total = jsc.accumulator(0);
        //step03 缓存单词结果集
        JavaRDD<String> allword = hamletRdd.flatMap(s -> splitWord(s).iterator()).cache();
        //step04 分析结果集,打印单词出现的次数最高10个
        System.out.println("word count top 10: ");
        allword.mapToPair(x -> {
            total.add(1);
            if(bstop.getValue().contains(x)) count.add(1);
            return new Tuple2<>(x, 1);
        }).reduceByKey((m,n) -> m + n)
                .mapToPair(x -> new Tuple2<>(x._2,x._1))
                .sortByKey(false)
                .mapToPair(x -> new Tuple2<>(x._1,x._2))
                .take(10)
                .stream()
                .forEach(System.out::println);
        System.out.println("total word count: \n" + total);
        System.out.println("stop word count: \n" + count.value());
        /**
         * 输出结果：
         * word count top 10:
         * (991,the)
         * (703,and)
         * (631,to)
         * (626,of)
         * (607,I)
         * (503,you)
         * (468,a)
         * (441,my)
         * (414,in)
         * (387,HAMLET)
         * total word count:
         * 32898
         * stop word count:
         * 16460
         */
    }
}
