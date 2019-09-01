package com.vicgong.wordcount;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class WordCountPre {
    public static void main(String[] args) throws IOException {
        if (args.length != 3) System.exit(1);
        String masterUrl = args[0];
        String inputFile = args[1];
        String outputFile = args[2];

        //创建SparkContext
        SparkConf conf = new SparkConf().setMaster(masterUrl).setAppName("Spark-WordCount");
        JavaSparkContext jsc = new JavaSparkContext(conf);
        //读取输入目录的数据,分词
        JavaRDD<String> lines = jsc.textFile(inputFile).flatMap(new FlatMapFunction<String, String>() {
            @Override
            public Iterator<String> call(String s) throws Exception {
                return Arrays.asList(s.split(" ")).iterator();
            }
        });
        //过滤掉无用数据
        JavaRDD<String> word = lines.filter(new Function<String, Boolean>() {
            @Override
            public Boolean call(String s) throws Exception {
                return s.length() > 0;
            }
        });
        //转为<Key,Value>对
        JavaPairRDD<String,Integer> pairs = word.mapToPair(new PairFunction<String, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(String s) throws Exception {
                return new Tuple2<>(s, 1);
            }
        });
        //汇总每个词出现的频率
        JavaPairRDD<String,Integer> counts =  pairs.reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer integer, Integer integer2) throws Exception {
                return integer + integer2;
            }
        });
        //判断输出目录是否存在如果存在则先删除
        FileSystem fs = FileSystem.get(new Configuration());
        Path outputPath = new Path(outputFile);
        if(fs.exists(outputPath)){
            fs.delete(outputPath,true);
        }
        //归并为1个partition输出一个文件
        counts.repartition(1).saveAsTextFile(outputFile);
        jsc.stop();
        //将结果RDD转化为数组，遍历输出，用于调试
        List<Tuple2<String, Integer>> output = counts.collect();
        for (Tuple2<?,?> tuple : output) {
            System.out.println(tuple._1() + ": " + tuple._2());
        }
    }
}
