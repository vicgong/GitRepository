package com.vicgong.Stream;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MapDemo {
    public static void main(String[] args) {
        //转大写
        List<String> wordList = Arrays.asList("hello","java");
        List<String> upperList = wordList.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());
        //平方数
        List<Integer> intList =  Arrays.asList(1, 2, 3, 4);
        List<Integer> square = intList.stream()
                .map(n -> n * n)
                .collect(Collectors.toList());
        intList.stream().forEach(System.out::println);
    }
}
