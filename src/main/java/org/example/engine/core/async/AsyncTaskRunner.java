package org.example.engine.core.async;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncTaskRunner {

    public static void submit(AsyncTask task) {
        Thread spawningThread = new Thread(() -> {
            try {
                process(task);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        spawningThread.setDaemon(true);
        spawningThread.start();
    }

    private static Thread process(AsyncTask task) {
        List<Thread> preRequisiteThreads = new ArrayList<>();
        for (AsyncTask prerequisite : task.prerequisites) {
            Thread prerequisiteThread = process(prerequisite);
            preRequisiteThreads.add(prerequisiteThread);
        }

        for (Thread prerequisiteThread : preRequisiteThreads) {
            try {
                prerequisiteThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Thread taskThread = new Thread(task::task);
        taskThread.setDaemon(true);
        taskThread.start();
        return taskThread;
    }

}
