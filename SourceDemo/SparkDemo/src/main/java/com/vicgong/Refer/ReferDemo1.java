package com.vicgong.Refer;

import java.util.function.Consumer;

public class ReferDemo1 {
    public static void main(String[] args) {
        //Java8内存函数式接口 Consumer<T> 接收一个参数T无返回操作
        //传统Lambda表达式
        Consumer<String> consumer = (s) -> System.out.println(s);
        consumer.accept("Hi: 我是Lambda表达式实现的!");
        //方法引用实现
        consumer = System.out::println;
        consumer.accept("Hello : 我是使用方法引用实现的!");
    }
}
