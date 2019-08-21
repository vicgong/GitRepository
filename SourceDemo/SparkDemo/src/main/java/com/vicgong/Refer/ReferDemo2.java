package com.vicgong.Refer;

import java.util.function.Consumer;

public class ReferDemo2 {
    public static void output(String str){
        System.out.println(str);
    }

    public static void main(String[] args) {
        Consumer<String> consumer = s -> output(s);
        consumer.accept("lambda表达式写法");
        Consumer<String> consumer1 = ReferDemo2 :: output;
        consumer1.accept("方法引用写法");
    }
}
