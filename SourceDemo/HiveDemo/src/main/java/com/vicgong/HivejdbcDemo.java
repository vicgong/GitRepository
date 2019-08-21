package com.vicgong;

import java.sql.*;

public class HivejdbcDemo {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("org.apache.hive.jdbc.HiveDriver");
        String user = "bigdata";
        String passwd = "bigdata";
        //String url = "jdbc:hive2://192.168.1.111:10000/default";
        //Connection conn = DriverManager.getConnection(url, user, passwd);
        String url = "jdbc:hive2://192.168.1.111:10000";
        Connection conn = DriverManager.getConnection(url, user, passwd);
        conn.setSchema("default");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("show tables");
        while(rs.next()){
            System.out.println(rs.getString(1));
        }
        stmt.close();
        conn.close();
    }
}
