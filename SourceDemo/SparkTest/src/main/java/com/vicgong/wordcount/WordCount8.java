package com.vicgong.wordcount;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.io.IOException;
import java.util.Arrays;

public class WordCount8 {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) System.exit(1);
        String inputFile = args[0];
        String outputFile = args[1];
        SparkConf conf = new SparkConf().setAppName("Spark-WordCount8");
        JavaSparkContext jsc = new JavaSparkContext(conf);
        JavaPairRDD<String, Integer> result = jsc.textFile(inputFile)
                .flatMap(s -> Arrays.asList(s.split(" ")).iterator())
                .filter(s -> s.length() > 0)
                .mapToPair(s -> new Tuple2<>(s, 1))
                .reduceByKey((n,m) -> n + m);
        Path path = new Path(outputFile);
        FileSystem fs = FileSystem.get(new Configuration());
        if(fs.exists(path)){
            fs.delete(path,true);
        }
        result.repartition(1).saveAsTextFile(outputFile);
        // Just for debugging, NOT FOR PRODUCTION
        result.foreach( pair ->
                System.out.println(String.format("%s - %d", pair._1(), pair._2()))
        );
        jsc.stop();
    }
}
