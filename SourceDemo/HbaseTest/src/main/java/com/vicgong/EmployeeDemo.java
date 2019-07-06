package com.vicgong;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDemo {

    private static Connection connection = null;
    private static Configuration configuration = HBaseConfiguration.create();
    private EmployeeDemo() { }

    //获得configuration对象
    public static Configuration getConfiguration(){
        return configuration;
    }

    //单例获取连接
    public static Connection getConnection() {
        try {
            if (connection == null) {
                connection = ConnectionFactory.createConnection(configuration);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connection;
    }

    //关闭connection资源
    public static void close() {
        try {
            if (connection != null) connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //关闭Admin句柄
    public static void close(Admin admin) {
        try {
            if (admin != null) admin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //关闭Table句柄
    public static void close(Table table) {
        try {
            if (table != null) table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //关闭BufferedMutator异步句柄
    public static void close(BufferedMutator bufferedMutator){
        try {
           if(bufferedMutator != null) bufferedMutator.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //创建表
    public static void createTable(String tabName, String[] familys){
        Connection connection = getConnection();
        Admin admin = null;
        try {
            admin = connection.getAdmin();
            TableName tableName = TableName.valueOf(tabName);
            if (!admin.tableExists(tableName)) {
                HTableDescriptor tableDescriptor = new HTableDescriptor(tableName);
                HColumnDescriptor hColumnDescriptor = null;
                for (String family : familys) {
                    hColumnDescriptor = new HColumnDescriptor(Bytes.toBytes(family));
                    tableDescriptor.addFamily(hColumnDescriptor);
                }
                admin.createTable(tableDescriptor);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(admin);
        }
    }

    //删除表
    public static void deleteTable(String tabName){
        Connection connection = getConnection();
        Admin admin = null;
        try {
            admin = connection.getAdmin();
            TableName tableName = TableName.valueOf(tabName);
            if (admin.tableExists(tableName)) {
                if (!admin.isTableDisabled(tableName)) {
                    admin.disableTable(tableName);
                }
                admin.deleteTable(tableName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(admin);
        }
    }

    //插入一条记录
    public static void put(String tabName, String rowkey, String family, String column, String value){
        Connection connection = getConnection();
        Table table = null;
        try {
            table = connection.getTable(TableName.valueOf(tabName));
            Put put = new Put(Bytes.toBytes(rowkey));
            put.addColumn(Bytes.toBytes(family), Bytes.toBytes(column), Bytes.toBytes(value));
            table.put(put);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(table);
        }
    }

    //删除一条记录
    public static void delete(String tabName, String rowkey, String family, String column){
        Connection connection = getConnection();
        Table table = null;
        try {
            table = connection.getTable(TableName.valueOf(tabName));
            Delete delete = new Delete(Bytes.toBytes(rowkey));
            delete.addColumn(Bytes.toBytes(family), Bytes.toBytes(column));
            table.delete(delete);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(table);
        }
    }

    //读取一条记录
    public static void get(String tabName, String rowkey, String family, String column){
        Connection connection = getConnection();
        Table table = null;
        try {
            table = connection.getTable(TableName.valueOf(tabName));
            Get get = new Get(Bytes.toBytes(rowkey));
            get.addColumn(Bytes.toBytes(family), Bytes.toBytes(column));
            Result result = table.get(get);
            while (result.advance()) {
                Cell cell = result.current();
                String value = Bytes.toString(cell.getValueArray(),cell.getValueOffset(),cell.getValueLength());
                System.out.println(family + ":" + column + " => " + value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(table);
        }
    }

    //批量读取记录
    public static void batchGet(String tabName, String[] rowkeys, String family, String column){
        Connection connection = getConnection();
        Table table = null;
        List<Get> list = new ArrayList<Get>();
        try {
            table = connection.getTable(TableName.valueOf(tabName));
            for(String rowkey : rowkeys ){
                Get get = new Get(Bytes.toBytes(rowkey));
                get.addColumn(Bytes.toBytes(family), Bytes.toBytes(column));
                list.add(get);
            }
            Result[] results = table.get(list);
            for(Result result : results){
                while (result.advance()) {
                    Cell cell = result.current();
                    String value = Bytes.toString(cell.getValueArray(),cell.getValueOffset(),cell.getValueLength());
                    System.out.println(family + ":" + column + " => " + value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(table != null) table.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //读取所有的指定列
    public static void scanAll(String tabName,String family, String column){
        Connection connection = getConnection();
        Table table = null;
        ResultScanner results = null;
        try {
            table = connection.getTable(TableName.valueOf(tabName));
            Scan scan = new Scan();
            scan.addColumn(Bytes.toBytes(family), Bytes.toBytes(column));
            scan.setCaching(100);
            results = table.getScanner(scan);
            for (Result result : results) {
                while (result.advance()) {
                    Cell cell = result.current();
                    String value = Bytes.toString(cell.getValueArray(),cell.getValueOffset(),cell.getValueLength());
                    System.out.println(family + ":" + column + " => " + value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            results.close();
            close(table);
        }
    }

    public void batchPut(String tabname) {
        String[] rowkeys = {"e004","e005","e006"};
        String[] profiles_name = {"Alice","Tom","Lily"};
        String[] profiles_age = {"31", "34", "58"};
        String[] departnemts_name = {"finance","legal","market"};
        String[] incomes_salary = {"7000","6000","12000"};
        String[] incomes_tax = {"125","100","300"};
        List<Put> list = new ArrayList<Put>();
        Put put = null;
        Table table = null;
        try {
            table = connection.getTable(TableName.valueOf(tabname));
            for(int i=0; i<rowkeys.length;i++){
                put = new Put(Bytes.toBytes(rowkeys[i]));
                put.addColumn(Bytes.toBytes("profile"),Bytes.toBytes("name"),Bytes.toBytes(profiles_name[i]));
                put.addColumn(Bytes.toBytes("profile"),Bytes.toBytes("age"),Bytes.toBytes(profiles_age[i]));
                put.addColumn(Bytes.toBytes("department"),Bytes.toBytes("name"),Bytes.toBytes(departnemts_name[i]));
                put.addColumn(Bytes.toBytes("income"),Bytes.toBytes("salary"),Bytes.toBytes(incomes_salary[i]));
                put.addColumn(Bytes.toBytes("income"),Bytes.toBytes("tax"),Bytes.toBytes(incomes_tax[i]));
                list.add(put);
            }
            table.put(list);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(table != null) table.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void insertEmpolyee(String tabname){
        String[] rowkeys = {"e001","e002","e003"};
        String[] profiles_name = {"Alice","Tom","Lily"};
        String[] profiles_age = {"31", "34", "58"};
        String[] departnemts_name = {"finance","legal","market"};
        String[] incomes_salary = {"7000","6000","12000"};
        String[] incomes_tax = {"125","100","300"};
        //异步插入数据,a async batch handler
        BufferedMutator bufferedMutator = null;
        Put put = null;
        try {
            bufferedMutator = connection.getBufferedMutator(TableName.valueOf(tabname));
            for(int i=0; i<rowkeys.length;i++){
                put = new Put(Bytes.toBytes(rowkeys[i]));
                put.addColumn(Bytes.toBytes("profile"),Bytes.toBytes("name"),Bytes.toBytes(profiles_name[i]));
                put.addColumn(Bytes.toBytes("profile"),Bytes.toBytes("age"),Bytes.toBytes(profiles_age[i]));
                put.addColumn(Bytes.toBytes("department"),Bytes.toBytes("name"),Bytes.toBytes(departnemts_name[i]));
                put.addColumn(Bytes.toBytes("income"),Bytes.toBytes("salary"),Bytes.toBytes(incomes_salary[i]));
                put.addColumn(Bytes.toBytes("income"),Bytes.toBytes("tax"),Bytes.toBytes(incomes_tax[i]));
                bufferedMutator.mutate(put);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            EmployeeDemo.close(bufferedMutator);
        }
    }

    public static void main(String[] args) {
        Configuration conf = EmployeeDemo.getConfiguration();
        conf.set("hbase.rootdir", "hdfs://192.168.1.111:9000/hbase");
        conf.set("hbase.zookeeper.quorum", "192.168.1.111");
        conf.set("hbase.zookeeper.property.clientPort", "2181");
        conf.set("zookeeper.znode.parent", "/hbase");
        String tabname = "employee";
        String[] family = {"profile","department","income"};
        EmployeeDemo employeeDemo = new EmployeeDemo();
        EmployeeDemo.createTable(tabname, family);
        //employeeDemo.insertEmpolyee(tabname);
        //get(tabname,"e001","income","salary");
        //scanAll(tabname,"profile","name");
        employeeDemo.batchPut(tabname);
        close();
    }
}
