package org.example.engine.core.async;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncTaskRunner {

    public void submit(AsyncTask task) throws Exception {
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

    private Thread process(AsyncTask task) throws Exception {
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
