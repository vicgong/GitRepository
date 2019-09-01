package com.vicgong.movie;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import scala.Tuple2;
import scala.Tuple3;

import java.util.*;

/**
 * 年龄段在“18-24”的男性年轻人，最喜欢看哪10部电影
 * 最喜欢：即电影观看次数最高的10部
 * users.dat UserID::Gender::Age::Occupation::Zip-code
 * ratings.dat UserID::MovieID::Rating::Timestamp
 * movies.dat MovieID::Title::Genres
 */
public class JavaPopularMovieAnalyzer {
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
        JavaRDD<String> usersRdd = jsc.textFile(DATA_PATH + "/users.dat", 2);
        JavaRDD<String> moviesRdd = jsc.textFile(DATA_PATH + "/movies.dat", 2);
        JavaRDD<String> ratingsRdd = jsc.textFile(DATA_PATH + "/ratings.dat", 2);
        //step02 Extract columns from RDDs
        //user: RDD[userID, Gender, Age]
        JavaRDD<Tuple3<String, String, String>> user = usersRdd
                .map(s -> s.split("::"))
                .map(s -> new Tuple3<>(s[0],s[1],s[2]))
                .filter(x -> x._2().equals("M"))
                .filter(x -> {
                    int age = Integer.parseInt(x._3());
                    return age >= 18 && age <= 24;
                });
        //List[String]
        List<String> userlist = user.map(x -> x._1()).collect();
        //broadcast
        Set<String> userSet = new HashSet<>();
        userSet.addAll(userlist); // userIds
        Broadcast<Set<String>> broadcastUserSet = jsc.broadcast(userSet);
        //List<Tuple2<MovieID,count>> Top 10
        List<Tuple2<String,Integer>> topKmovies = ratingsRdd
                .map(x -> x.split("::"))
                .mapToPair(x -> new Tuple2<String, String>(x[0], x[1])) // (userId,movieId)
                .filter(x -> broadcastUserSet.getValue().contains(x._1))
                .mapToPair(x -> new Tuple2<String, Integer>(x._2(), 1)) // (movieId, count)
                .reduceByKey((x, y) -> x + y) // (movieId, count)
                .mapToPair(x -> new Tuple2<>(x._2, x._1)) // (count, movieId)
                .sortByKey(false) // sort by count desc
                .mapToPair(x -> new Tuple2<>(x._2, x._1)) // (movieId, count)
                .take(10);
        //step03 Transfrom filmID to fileName
        List<Tuple2<String,String>> movieID2NameList = moviesRdd
                .map(s -> s.split("::"))
                .mapToPair(s -> new Tuple2<>(s[0],s[1]))
                .collect();
        Map<String, String> movieID2Name = new HashMap<>();
        movieID2NameList.stream().forEach(x -> movieID2Name.put(x._1, x._2));
        topKmovies.stream()
                .map(x -> movieID2Name.getOrDefault(x._1, null) + "," + x._2)
                .forEach(x -> System.out.println(x));
        jsc.stop();
    }
}
