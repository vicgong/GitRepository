package com.vicgong;

import java.sql.*;
import java.util.Properties;

public class PrestoJDBCDemo {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        /**
         * Simple Example
         */
        Class.forName("com.facebook.presto.jdbc.PrestoDriver");
        String url = "jdbc:presto://192.168.1.111:8081";
        Connection connection = DriverManager.getConnection(url, "bigdata", null);
        connection.setCatalog("hive");
        connection.setSchema("default");
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("show tables");
        while(rs.next()){
            System.out.println(rs.getString(1));
        }
        rs.close();
        connection.close();

        /**
         * URL parameters
         * SSL->true : Use HTTPS for connections
         */
        //String url = "jdbc:presto://example.net:8080/hive/sales";
        //Properties properties = new Properties();
        //properties.setProperty("user", "test");
        //properties.setProperty("password", "secret");
        //properties.setProperty("SSL", "true"); //
        //Connection connection1 = DriverManager.getConnection(url, properties);

        /**
         * properties
         * SSL->true 通过request参数形式
         */
        //String url1 = "jdbc:presto://example.net:8080/hive/sales?user=bigdata&password=secret&SSL=true";
        //Connection connection2 = DriverManager.getConnection(url1);
    }
}
