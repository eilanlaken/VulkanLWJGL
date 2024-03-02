package org.example.engine.core.async;

import java.util.ArrayList;
import java.util.List;

public final class RunnerTaskAsync {

    public static void submit(TaskAsync task) {
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

    private static Thread start(TaskAsync task) {
        List<Thread> preRequisiteThreads = new ArrayList<>();
        for (TaskAsync prerequisite : task.prerequisites) {
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

        Thread taskThread = new Thread(task::start);
        taskThread.setDaemon(true);
        taskThread.start();
        return taskThread;
    }

}