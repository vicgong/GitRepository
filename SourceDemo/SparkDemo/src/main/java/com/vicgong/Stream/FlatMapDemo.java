package com.vicgong.Stream;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FlatMapDemo {
    public static void main(String[] args) throws FileNotFoundException {
        //留下偶数
        Integer[] sixNums = {1, 2, 3, 4, 5, 6};
        Integer[] evens = Stream.of(sixNums)
                .filter(n -> n%2 == 0)
                .toArray(Integer[]::new);

        //把单词挑出来
        BufferedReader reader = new BufferedReader(
                new FileReader("C:\\Users\\龚明达\\IdeaProjects\\SparkDemo\\data\\word.txt"));
        List<String> output = reader.lines()
                .flatMap(line -> Stream.of(line.split(" ")))
                .filter(word -> word.length() > 0)
                .collect(Collectors.toList());
        output.stream().forEach(System.out::println);
    }
}
