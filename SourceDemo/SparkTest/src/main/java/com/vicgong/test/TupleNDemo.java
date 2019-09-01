package com.vicgong.test;
import scala.Tuple2;
import scala.collection.Iterator;

public class TupleNDemo {
    public static void main(String[] args) {
        //定义2元素元组
        Tuple2<Integer,String> tuple2 = new Tuple2(1,"Hello");
        //获取第一个元素
        System.out.println(tuple2._1());
        System.out.println(tuple2._1);
        //获取第二个元素
        System.out.println(tuple2._2());
        System.out.println(tuple2._2);
        //转字符串，输出结果：(1,Hello)
        System.out.println(tuple2.toString());
        //迭代输出元素内容
        Iterator it = tuple2.productIterator();
        while(it.hasNext()){
            System.out.println(it.next());
        }
        //交换Tuple2中的元素,输出(Hello,1)
        System.out.println(tuple2.swap());
    }
}
