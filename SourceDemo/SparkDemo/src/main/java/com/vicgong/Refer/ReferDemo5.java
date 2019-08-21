package com.vicgong.Refer;

        import java.util.function.Function;

public class ReferDemo5 {
    public static void main(String[] args) {
        /**
         *  java8内置函数式接口Function<T, R>
         *  传入一个参数值T返回R类型的实例
         */
        //传统Lambda实现
        Function<Integer,int[]> function = (i) -> new int[i];
        int[] apply = function.apply(5);
        System.out.println(apply.length); // 5
        //数组类型引用实现
        function = int[] :: new;
        apply = function.apply(10);
        System.out.println(apply.length); // 10
    }
}
