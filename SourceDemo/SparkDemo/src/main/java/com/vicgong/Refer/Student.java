package com.vicgong.Refer;

import java.util.function.Supplier;

public class Student {
    public String name;
    public Student(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
    public String setName(String name){
        return this.name = name;
    }

    public static void main(String[] args) {
        Student student = new Student("vicgong");
        //Java8内置的函数式接口Supplier<T>，返回一个T类型的结果
        Supplier<String> supplier = () -> student.getName();
        System.out.println("Lambda形式： " + supplier.get());
        //由于方法体内容已通过student.getName可使用方法引用
        Supplier<String> supplier1 = student :: getName;
        System.out.println("方法引用形式： "+supplier1.get());
    }
}
