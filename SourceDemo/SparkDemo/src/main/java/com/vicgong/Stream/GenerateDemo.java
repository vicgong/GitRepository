package com.vicgong.Stream;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class GenerateDemo {

    public static void main(String[] args) throws IOException {
        //1.通过Collection及其子类构造source
        List<String> list = Arrays.asList("a","b","c");
        Stream<String> stream1 = list.stream();
        Stream<String> stream2 = list.parallelStream();
        stream1.forEach(s -> System.out.println(s + "\t"));
        stream2.forEach(s -> System.out.println(s + "\t"));

        //2.通过数组构造source
        String[] strArray = new String[]{"a","b","c"};
        Stream<String> stream3 = Arrays.stream(strArray);
        Stream<String> stream4 = Stream.of(strArray);
        Stream<String> stream5 = Stream.of("a","b","c");
        stream3.forEach(s -> System.out.println(s + "\t"));
        stream4.forEach(s -> System.out.println(s + "\t"));
        stream5.forEach(s -> System.out.println(s + "\t"));

        //3.通过静态工厂构造source
        Path path = Paths.get("C:\\Users\\龚明达\\IdeaProjects\\SparkDemo\\src");
        Stream<Path> stream6 = Files.walk(path, FileVisitOption.FOLLOW_LINKS);
        stream6.forEach(s -> System.out.println(s.toFile().getAbsolutePath() + "\t"));

        //4.数值流
        IntStream.of(1, 2, 3).forEach(System.out::print);
        IntStream.of(new int[]{1, 2, 3}).forEach(System.out::print);
        IntStream.range(1,3).forEach(System.out::print);
    }
}
