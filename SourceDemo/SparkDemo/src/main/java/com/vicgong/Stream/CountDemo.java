package com.vicgong.Stream;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class CountDemo {
    public static void main(String[] args) throws IOException {
        long count = Arrays.asList("a","b","c").stream().count();
        System.out.println(count);
    }
}
