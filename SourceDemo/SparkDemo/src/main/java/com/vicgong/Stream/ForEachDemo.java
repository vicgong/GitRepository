package com.vicgong.Stream;

import java.util.Arrays;
import java.util.List;

public class ForEachDemo {
    public static void main(String[] args) {
        List<Person> roster = Arrays.asList(
                new Person("kerr","女"),
                new Person("vicgong", "男"));
        // Java 8
        roster.stream()
                .filter(p -> p.getGender() == Person.Sex.MALE)
                .forEach(p -> System.out.println(p.getName()));
        // Pre-Java 8
        for (Person p : roster) {
            if (p.getGender() == Person.Sex.MALE) {
                System.out.println(p.getName());
            }
        }
    }
}
class Person{
    public static SEX Sex;
    public String name;
    public String gender;
    public Person(String name, String gender) {
        this.name = name;
        this.gender = gender;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
}
class SEX {
    public static final String MALE = "男";
    public static final String FEMALE = "女";
}

