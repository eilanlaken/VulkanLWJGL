package org.example.engine.core.async;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.physics2d_new.Physics2DException;

import java.util.*;

public class AsyncTaskRunner {

    public static <T extends AsyncTask> Thread[] async(T ...tasks) {
        CollectionsArray<Thread> tThreads = new CollectionsArray<>();
        for (T task : tasks) {
            tThreads.add(async(task));
        }
        return tThreads.toArray(Thread.class);
    }

    public static <T extends AsyncTask> Thread[] async(Iterable<T> tasks) {
        CollectionsArray<Thread> tThreads = new CollectionsArray<>();
        for (AsyncTask task : tasks) {
            tThreads.add(async(task));
        }
        return tThreads.toArray(Thread.class);
    }

    public static <T extends AsyncTask> Thread async(T task) {
        if (task.prerequisites != null && !task.prerequisites.isEmpty()) {
            List<Thread> pThreads = new ArrayList<>();
            for (AsyncTask prerequisite : task.prerequisites) {
                Thread pThread = async(prerequisite);
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

    public static void await(Thread ...threads) {
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new Physics2DException(e.getLocalizedMessage());
            }
        }
    }

    public static void await(Iterable<Thread> threads) {
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new Physics2DException(e.getLocalizedMessage());
            }
        }
    }

    @Deprecated public static void runSync(AsyncTask ...tasks) {
        for (AsyncTask task : tasks) runSync(task);
    }

    @Deprecated public static void runSync(Iterable<AsyncTask> tasks) {
        for (AsyncTask task : tasks) runSync(task);
    }

    @Deprecated public static void runSync(AsyncTask task) {
        for (AsyncTask preRequisite : task.prerequisites) {
            runSync(preRequisite);
        }
        task.run();
    }
    
}
