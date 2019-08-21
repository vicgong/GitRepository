package com.vicgong.Stream;

import java.util.ArrayList;
import java.util.List;

public class MatchDemo {
    public static void main(String[] args) {
        List<NPerson> persons = new ArrayList();
        persons.add(new NPerson(1, "name" + 1, 10));
        persons.add(new NPerson(2, "name" + 2, 21));
        persons.add(new NPerson(3, "name" + 3, 34));
        persons.add(new NPerson(4, "name" + 4, 6));
        persons.add(new NPerson(5, "name" + 5, 55));
        boolean isAllAdult = persons.stream().
                allMatch(p -> p.getAge() > 18);
        System.out.println("All are adult? " + isAllAdult);
        boolean isThereAnyChild = persons.stream().
                anyMatch(p -> p.getAge() < 12);
        System.out.println("Any child? " + isThereAnyChild);
    }
}
