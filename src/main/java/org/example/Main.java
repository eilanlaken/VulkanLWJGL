package org.example;

import org.example.engine.core.application.Application;
import org.example.engine.core.async.RunnerTaskSync;
import org.example.engine.core.async.TaskAsync;
import org.example.engine.core.async.RunnerTaskAsync;
import org.example.engine.core.async.TaskSync;
import org.example.engine.core.graphics.WindowAttributes;
import org.example.game.WindowScreenTest_AssetStore_1;

public class Main {

    public static void main(String[] args) {
        //testCode1();
        testCode2();

        WindowAttributes config = new WindowAttributes();
        Application.createSingleWindowApplication(config);
        Application.launch(new WindowScreenTest_AssetStore_1());
    }

    private static void testCode1() {
        TaskAsync taskAsyncChildAAA = new TaskAsync("child AAA", 5);
        TaskAsync taskAsyncChildAA = new TaskAsync("child AA", 5, taskAsyncChildAAA);
        TaskAsync taskAsyncChildA = new TaskAsync("child A", 3, taskAsyncChildAA);
        TaskAsync taskAsyncChildB = new TaskAsync("child B", 6);
        TaskAsync parent = new TaskAsync("parent", 2, taskAsyncChildB, taskAsyncChildA);
        RunnerTaskAsync.submit(parent);
    }

    private static void testCode2() {
        TaskSync a = new TaskSync() {
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

        TaskSync aa = new TaskSync(a) {
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

        TaskSync b = new TaskSync(a) {
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

        TaskSync c = new TaskSync(a,b) {
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

        TaskSync d = new TaskSync(aa,a,b,c) {
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

//        TaskSync aa = new TaskSync("aa",a);
//        TaskSync b = new TaskSync("b",a);
//        TaskSync c = new TaskSync("c,",a, b);
//        TaskSync d = new TaskSync("d,",aa, a, b, c);

//        System.out.println("a: " + TaskSync.countPrerequisites(a));
//        System.out.println("b: " + TaskSync.countPrerequisites(b));
//        System.out.println("c: " + TaskSync.countPrerequisites(c));
//        System.out.println("d: " + TaskSync.countPrerequisites(d));

        RunnerTaskSync.run(a,aa,d,c,b);
    }
}