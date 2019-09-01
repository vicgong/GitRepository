package com.vicgong.test;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.List;

/**
 * spark-sumit
 * spark-submit \
 * --class com.vicgong.test.ProcessDB3 \
 * --jars /home/bigdata/soft/mysql-connector-java-5.1.7-bin.jar \
 * /home/bigdata/AuraHadoopTraining/target/SparkTest-1.0-SNAPSHOT.jar
 */
public class ProcessDB3 {
    public static void main(String[] args) throws Exception {
        SparkConf conf = new SparkConf().setMaster("local").setAppName("VariableDemo");
        JavaSparkContext jsc = new JavaSparkContext(conf);
        List<Tuple3<String,String,Integer>> list = Arrays.asList(
                new Tuple3<>("20190831","192.168.1.111",20),
                new Tuple3<>("20190831","192.168.1.112",21),
                new Tuple3<>("20190831","192.168.1.113",22)
        );
        // logRDD contains (date, url, count), 类型是RDD[(String, String, Int)]
        JavaRDD<Tuple3<String, String, Integer>> logRDD = jsc.parallelize(list);
        logRDD.foreachPartition( x -> {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://192.168.1.106/sample","root","rootuse");
            String sql = "insert into table_logs(date, url, count) values(?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            while(x.hasNext()){
                Tuple3<String, String, Integer> tuple = x.next();
                pstmt.setString(1,tuple._1());
                pstmt.setString(2,tuple._2());
                pstmt.setInt(3,tuple._3());
                pstmt.execute();
            }
            conn.close();
        });
    }
}
