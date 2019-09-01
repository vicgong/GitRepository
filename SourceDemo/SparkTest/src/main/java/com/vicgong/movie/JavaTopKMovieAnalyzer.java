package com.vicgong.movie;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;
import scala.Tuple3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 得分最高的10部电影：即电影平均分最高的10部电影
 * 看过电影最多的前10个人：即用户观看电影数最多的10个人
 * ratings.dat UserID::MovieID::Rating::Timestamp
 * movies.dat MovieID::Title::Genres
 */
public class JavaTopKMovieAnalyzer {
    public static void main(String[] args) {
        String DATA_PATH = "file:////home/bigdata/tmp";
        String masterUrl = "local";
        if(args.length == 2){
            masterUrl = args[0];
            DATA_PATH = args[1];
        }
        SparkConf conf = new SparkConf().setMaster(masterUrl).setAppName("JavaPopularMovieAnalyzer");
        JavaSparkContext jsc = new JavaSparkContext(conf);
        //step01 数据加载
        JavaRDD<String> ratingsRdd = jsc.textFile(DATA_PATH + "/ratings.dat");
        JavaRDD<String> moviesRdd = jsc.textFile(DATA_PATH + "/movies.dat");
        //step02 Extract columns from RDDs
        //ratings: RDD[userID, MovieID, Rating]
        JavaRDD<Tuple3<String, String, String>> ratings = ratingsRdd
                .map(x -> x.split("::"))
                .map(x -> new Tuple3<>(x[0], x[1], x[2]))
                .cache();
        List<Tuple2<String, String>> movieList = moviesRdd
                .map(x -> x.split("::"))
                .mapToPair(x -> new Tuple2<>(x[0],x[1]))
                .collect();
        Map<String,String> movieID2Name = new HashMap<>();
        movieList.stream().forEach(p ->movieID2Name.put(p._1,p._2));
        //得分最高的10部电影
        ratings.mapToPair(x -> new Tuple2<String, Tuple2<Integer, Integer>>(x._2(),
                                new Tuple2<>(Integer.parseInt(x._3()), 1)))
                .reduceByKey((v1, v2) -> new Tuple2<>(v1._1 + v2._1, v1._2 + v2._2))
                .mapToPair(x -> new Tuple2<>(x._2._1 / x._2._2 + 0.0f, x._1))
                .sortByKey(false)
                .mapToPair(x -> new Tuple2<>(movieID2Name.getOrDefault(x._2,null), x._1))
                .take(10)
                .forEach(x -> System.out.println(x));
        //看过电影最多的前10个人
        ratings.mapToPair(x -> new Tuple2<>(x._2(),1))
                .reduceByKey((v1, v2) -> v1 + v2)
                .mapToPair(x -> new Tuple2<>(x._2,x._1))
                .sortByKey(false)
                .mapToPair(x -> new Tuple2<>(x._2, x._1))
                .take(10)
                .stream()
                .forEach(x -> System.out.println(x));
    }
}
