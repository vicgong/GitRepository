package com.vicgong.Lambda;

import java.util.Arrays;
import java.util.Comparator;

public class LambdaDemo {
    public static void main(String[] args) {
        //使用匿名内部类
        String[] datas2 = new String[]{"java","hi","lambda"};
        Arrays.sort(datas2, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return Integer.compare(o1.length(),o2.length());
            }
        });
        //使用含参的lambda替代内部类
        Arrays.sort(datas2,(o1, o2) -> Integer.compare(o1.length(),o2.length()));

        //使用匿名内部类
        String[] datas = new String[]{"a","b","d"};
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(datas);
            }
        }).start();

        //无参数的lambda替代内部类
        new Thread(() -> System.out.println(datas)).start();
    }
}
