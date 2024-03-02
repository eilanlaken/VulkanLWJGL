package org.example;

import org.example.engine.core.application.Application;
import org.example.engine.core.async.*;
import org.example.engine.core.graphics.WindowAttributes;
import org.example.game.WindowScreenTest_AssetStore_1;

public class Main {

    public static void main(String[] args) {
        testCode1();
        testCode2();

        WindowAttributes config = new WindowAttributes();
        Application.createSingleWindowApplication(config);
        Application.launch(new WindowScreenTest_AssetStore_1());
    }

    private static void testCode1() {
        Task childA = new Task() {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    System.out.println("childA");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Task childB = new Task() {
            @Override
            public void run() {
                for (int i = 0; i < 11; i++) {
                    System.out.println("childB");
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Task parent = new Task(childA, childB) {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    System.out.println("parent");
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onComplete() {
                System.out.println("parent completed");
            }
        };

        TaskRunner.runAsync(parent);
    }

    private static void testCode2() {
        Task a = new Task() {
            @Override
            public void run() {
                System.out.println("a");
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Task aa = new Task(a) {
            @Override
            public void run() {
                System.out.println("aa");
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Task b = new Task(a) {
            @Override
            public void run() {
                System.out.println("b");
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Task c = new Task(a,b) {
            @Override
            public void run() {
                System.out.println("c");
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Task d = new Task(aa,a,b,c) {
            @Override
            public void run() {
                System.out.println("d");
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        TaskRunner.runSync(a,aa,d,c,b);
    }
}