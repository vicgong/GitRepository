package com.vicgong.Optional;

import java.util.Optional;

public class OptionalDemo2 {
    public static void print(String text){
        // Java 8 值不为空时进行打印的写法更简单安全
        Optional.ofNullable(text).ifPresent(System.out::println);
        // Pre-Java 8
        if (text != null) {
            System.out.println(text);
        }
    }
    public static int getLength(String text) {
        // Java 8 对空值的处理写法更简单
        return Optional.ofNullable(text).map(String::length).orElse(-1);
        // Pre-Java 8
        // return if (text != null) ? text.length() : -1;
    };
    public static void main(String[] args) {
        String strA = " abcd ", strB = null;
        print(strA); print(""); print(strB);
        getLength(strA); getLength(""); getLength(strB);
    }
}
