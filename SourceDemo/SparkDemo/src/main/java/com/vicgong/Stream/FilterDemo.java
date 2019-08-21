package com.vicgong.Stream;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FilterDemo {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(
                	        new FileReader("C:\\Users\\龚明达\\IdeaProjects\\SparkDemo\\data\\word.txt"));
        List<String> output = reader.lines()
        	        .flatMap(line -> Stream.of(line.split(" ")))
        	        .filter(word -> word.length() > 0)
        	        .collect(Collectors.toList());
        	output.stream().forEach(System.out::println);
        reader.close();
    }
}
