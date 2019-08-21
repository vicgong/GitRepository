package com.vicgong.Stream;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChangeDemo {
    public static void main(String[] args) {
        //1、流转换为数组
        Stream<String> stream = Stream.of("a","b","c");
        String[] arr = stream.toArray(String[]::new);
        //2.流转换为Collection
        stream = Stream.of("a","b","c");
        List<String> list = stream.collect(Collectors.toList());
        stream = Stream.of("a","b","c");
        Set<String> set = stream.collect(Collectors.toSet());
        stream = Stream.of("a","b","c");
        List<String> arrList = stream.collect(Collectors.toCollection(ArrayList::new));
        //3.流转换为String
        stream = Stream.of("a","b","c");
        String str = stream.collect(Collectors.joining());
        System.out.print(str);
    }
}
