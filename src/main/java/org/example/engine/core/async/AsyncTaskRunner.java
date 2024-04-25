package org.example.engine.core.async;

import java.util.*;

public class AsyncTaskRunner {

    public static void runAsync(Iterable<AsyncTask> tasks) {
        for (AsyncTask task : tasks) runAsync(task);
    }

    public static void runAsync(AsyncTask task) {
        Thread spawningThread = new Thread(() -> {
            try {
                start(task);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        spawningThread.setDaemon(true);
        spawningThread.start();
    }

    private static Thread start(AsyncTask task) {
        List<Thread> preRequisiteThreads = new ArrayList<>();
        for (AsyncTask prerequisite : task.prerequisites) {
            Thread prerequisiteThread = start(prerequisite);
            preRequisiteThreads.add(prerequisiteThread);
        }

        for (Thread prerequisiteThread : preRequisiteThreads) {
            try {
                prerequisiteThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Thread taskThread = new Thread(task::run);
        taskThread.setDaemon(true);
        taskThread.start();
        return taskThread;
    }

    public static void runSync(Iterable<AsyncTask> tasks) {
        for (AsyncTask task : tasks) runSync(task);
    }

    public static void runSync(AsyncTask task) {
        for (AsyncTask preRequisite : task.prerequisites) {
            runSync(preRequisite);
        }
        task.run();
    }
    
}
