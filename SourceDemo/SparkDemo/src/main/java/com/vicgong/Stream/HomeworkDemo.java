package com.vicgong.Stream;

import java.util.stream.IntStream;

public class HomeworkDemo {
    public static void main(String[] args) {
        //保存了99 到9999之间的整数
        //按行打印其中的奇数 求出所有元素的和
        int sum = IntStream.range(99, 9999).peek(value -> {
            if(value % 2 != 0) {
                System.out.print(value + "\t");
            }
        }).sum();
        System.out.println("\nsum:" + sum);
    }
}
