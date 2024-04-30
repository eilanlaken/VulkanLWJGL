package org.example.engine.core.async;

import org.example.engine.core.collections.CollectionsArray;

import java.util.*;

public class AsyncTaskRunner {

    public static <T extends AsyncTask> Thread[] runAsync(T ...tasks) {
        CollectionsArray<Thread> tThreads = new CollectionsArray<>();
        for (T task : tasks) {
            tThreads.add(runAsync(task));
        }
        return tThreads.toArray(Thread.class);
    }

    public static <T extends AsyncTask> Thread[] runAsync(Iterable<T> tasks) {
        CollectionsArray<Thread> tThreads = new CollectionsArray<>();
        for (AsyncTask task : tasks) {
            tThreads.add(runAsync(task));
        }
        return tThreads.toArray(Thread.class);
    }

    public static <T extends AsyncTask> Thread runAsync(T task) {
        if (task.prerequisites != null && !task.prerequisites.isEmpty()) {
            List<Thread> pThreads = new ArrayList<>();
            for (AsyncTask prerequisite : task.prerequisites) {
                Thread pThread = runAsync(prerequisite);
                pThreads.add(pThread);
            }

            for (Thread pThread : pThreads) {
                try {
                    pThread.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        Thread tThread = new Thread(task::run);
        tThread.setDaemon(true);
        tThread.start();
        return tThread;
    }

    public static void runSync(AsyncTask ...tasks) {
        for (AsyncTask task : tasks) runSync(task);
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
