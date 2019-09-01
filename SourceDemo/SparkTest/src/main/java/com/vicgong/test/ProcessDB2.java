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

public class ProcessDB2 {
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
        logRDD.foreach( x -> {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://192.168.1.106/sample","root","rootuse");
            String sql = "insert into table_logs(date, url, count) values(?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,x._1());
            pstmt.setString(2,x._2());
            pstmt.setInt(3,x._3());
            pstmt.execute();
            conn.close();
        });

    }
}
