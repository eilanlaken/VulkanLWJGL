package org.example;

import org.example.engine.Application;
import org.example.engine.core.collections.ArrayLong;

public class Main {
    public static void main(String[] args) {

        ArrayLong a = new ArrayLong();
        a.add(1);
        a.add(1);
        a.add(2);
        System.out.println("hash 1: " + a.hashCode());

        ArrayLong b = new ArrayLong();
        b.add(9);
        b.add(1);
        b.add(2);
        b.add(5);
        System.out.println("hash 2: " + b.hashCode());




        Application application = new Application("My Game", true);
        application.launch();
    }
}