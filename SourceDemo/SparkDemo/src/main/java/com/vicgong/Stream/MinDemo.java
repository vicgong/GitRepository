package com.vicgong.Stream;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MinDemo {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\龚明达\\IdeaProjects\\SparkDemo\\data\\word.txt"));
        int longest = br.lines()
                .mapToInt(String::length)
                .min()
                .getAsInt();
        br.close();
        System.out.println(longest);
    }
}
