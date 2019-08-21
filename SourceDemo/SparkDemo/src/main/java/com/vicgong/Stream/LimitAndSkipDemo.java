package com.vicgong.Stream;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LimitAndSkipDemo {
    public static void main(String[] args) {
        List<NPerson> persons = new ArrayList();
        for (int i = 1; i <= 10000; i++) {
            NPerson person = new NPerson(i, "name" + i, 10);
            persons.add(person);
        }
        List<String> personList2 = persons.stream()
                .map(NPerson::getName)
                .limit(10)
                .skip(3)
                .collect(Collectors.toList());
        System.out.println(personList2);
    }

}
class NPerson {
    public int no;
    private String name;
    private int age;
    public NPerson(int no, String name, int age) {
        this.no = no;
        this.name = name;
        this.age = age;
    }
    public String getName() {
        System.out.println(name);
        return name;
    }
    public int getAge() {
        return age;
    }
}
