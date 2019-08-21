package com.vicgong.Stream;

import java.util.Arrays;

public class FindAnyDemo {
    public static void main(String[] args) {
        Arrays.asList("a","b","c").stream()
                .findAny()
                .ifPresent(System.out::println);
    }
}
