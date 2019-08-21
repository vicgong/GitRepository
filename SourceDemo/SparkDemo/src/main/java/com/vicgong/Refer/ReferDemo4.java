package com.vicgong.Refer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ReferDemo4 {
    /**
     *  java8内置接口Supplier<T>
     *  无参数，返回一个T类型结果
     */
    //传统lambda写法
    Supplier<List<String>> supplier = () -> new ArrayList<String>();
    List<String> list = supplier.get();

    //使用构造器引用
    Supplier<List<String>> supplier1 = ArrayList::new;
    List<String> list1 = supplier1.get();
}
