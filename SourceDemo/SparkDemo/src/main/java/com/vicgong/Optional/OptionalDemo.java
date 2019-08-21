package com.vicgong.Optional;

import java.util.Optional;

public class OptionalDemo {
    public static void main(String[] args) {
        //1 创建空的optional
        Optional optional = Optional.empty();
        System.out.println(optional.isPresent());
        //2 Optional.ofNullable - 允许传递为 null 参数
        Integer value1 = null;
        Optional opt1 = Optional.ofNullable(value1);
        //3 Optional.of - 如果传递的参数是 null，抛出异常 NullPointerException
        Integer value2 = new Integer(10);
        Optional opt2 = Optional.of(value2);
        //4 执行Optional参数的函数
        System.out.print(sum(opt1,opt2));
    }
    public static Integer sum(Optional<Integer> a, Optional<Integer> b){

        // Optional.isPresent - 判断值是否存在
        System.out.println("第一个参数值存在: " + a.isPresent());
        System.out.println("第二个参数值存在: " + b.isPresent());
        // Optional.orElse - 如果值存在，返回它，否则返回默认值
        Integer value1 = a.orElse(new Integer(0));
        //Optional.get - 获取值，值需要存在
        Integer value2 = b.get();
        return value1 + value2;
    }
}
