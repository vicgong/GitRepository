package com.vicgong.movie;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

/**
 * 看过“Lord of the Rings, The (1978)”用户年龄和性别分布
 * users.dat UserID::Gender::Age::Occupation::Zip-code
 * ratings.dat UserID::MovieID::Rating::Timestamp
 */
public class JavaMovieUserAnalyzer {
    public static void main(String[] args) {
        String DATA_PATH = "file:////home/bigdata/tmp";
        String MOVIE_TITLE = "Lord of the Rings, The (1978)";
        final String MOVIE_ID = "2116";
        String masterUrl = "local";
        if(args.length == 2){
            masterUrl = args[0];
            DATA_PATH = args[1];
        }
        SparkConf conf = new SparkConf().setMaster(masterUrl).setAppName("JavaMovieUserAnalyzer");
        JavaSparkContext jsc = new JavaSparkContext(conf);
        //step01 数据加载
        JavaRDD<String> usersRDD = jsc.textFile(DATA_PATH + "/users.dat");
        JavaRDD<String> ratingsRDD = jsc.textFile(DATA_PATH + "/ratings.dat");
        //step02 Extract columns from RDDs
        //user: RDD[userID,gender:age]
        JavaPairRDD<String, String> user = usersRDD.mapToPair(s -> {
            String[] arr = s.split("::");
            return new Tuple2<>(arr[0],arr[1] + ":" + arr[2]);
        });
        //ratings: RDD[userID,movieID]
        JavaPairRDD<String, String> ratings = ratingsRDD.mapToPair(s -> {
            String[] arr = s.split("::");
            return new Tuple2<>(arr[0],arr[2]);
        }).filter(p -> p._2.equals(MOVIE_ID));
        //step03 join RDDs
        //useRating: RDD[(userID, (movieID, gender:age)]
        JavaPairRDD<String, Tuple2<String,String>> useRating = user.join(ratings);
        //userDistribution: RDD[(gender:age, count)]
        JavaPairRDD<String, Integer> result = useRating.mapToPair(s -> new Tuple2<>(s._2._2,1))
                .reduceByKey((m,n) -> m + n);
        result.collect().stream().forEach(t ->
                System.out.println("gender & age:" + t._1() + ", count:" + t._2())
        );
        jsc.stop();
    }
}
