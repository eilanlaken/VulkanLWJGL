package org.example;

import org.example.engine.Application;

public class Main {
    public static void main(String[] args) {
        Application application = new Application("My Game", true);
        application.launch();
    }
}