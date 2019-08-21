package com.vicgong.Refer;

import java.util.function.BiPredicate;

public class ReferDemo3 {
    public static void main(String[] args) {
        /**
         *  java8内置函数式接口BiPredicate<T,U>
         *  传入两个参数值，返回一个boolean值结果
         */
        //传统lambda写法
        BiPredicate<String,String> predicate = (s, s2) -> s.equals(s2);
        System.out.println(predicate.test("vic","vicgong"));
        //引用方法写法
        BiPredicate<String,String> predicate1 = String :: equals;
        System.out.println(predicate1.test("vic","vicgong"));
    }
}
