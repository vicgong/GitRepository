package com.vicgong.Stream;

import java.util.Arrays;

public class FindFirstDemo {
    public static void main(String[] args) {
        Arrays.asList("a","b","c").stream()
                .findFirst()
                .ifPresent(System.out::println);
    }
}
