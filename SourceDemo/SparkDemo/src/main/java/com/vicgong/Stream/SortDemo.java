package com.vicgong.Stream;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SortDemo {
    public static void main(String[] args) {
        List<NPerson> persons = new ArrayList();
        for (int i = 1; i <= 5; i++) {
            NPerson person = new NPerson(i, "name" + i, 10);
            persons.add(person);
        }
        List<NPerson> personList2 = persons.stream()
                .limit(2)
                .sorted((p1, p2) -> p1.getName().compareTo(p2.getName()))
                .collect(Collectors.toList());
        System.out.println(personList2);
    }
}
