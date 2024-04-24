package org.example.engine.core.async;

import java.util.*;

// TODO: test
public class AsyncTaskRunner {

    public static void runAsync(AsyncTask...tasks) {
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

    // TODO: test. probably sucks.
    public static void runSync(AsyncTask...tasks) {
        TaskPriority[] taskPriorities = new TaskPriority[tasks.length];
        for (int i = 0; i < taskPriorities.length; i++) {
            taskPriorities[i] = new TaskPriority();
            taskPriorities[i].task = tasks[i];
            taskPriorities[i].priority = countPrerequisites(tasks[i]);
        }
        Arrays.sort(taskPriorities, Comparator.comparingInt(p -> p.priority));
        for (TaskPriority taskPriority : taskPriorities) taskPriority.task.run();
    }


    private static int countPrerequisites(AsyncTask task) {
        Set<AsyncTask> allDistinctPrerequisites = new HashSet<>();
        collectDistinctPrerequisites(task, allDistinctPrerequisites);
        return allDistinctPrerequisites.size();
    }

    private static void collectDistinctPrerequisites(AsyncTask task, Set<AsyncTask> allDistinctPrerequisites) {
        if (task == null) return;
        for (AsyncTask prerequisite : task.prerequisites) {
            allDistinctPrerequisites.add(prerequisite);
            collectDistinctPrerequisites(prerequisite, allDistinctPrerequisites);
        }
    }

    private static class TaskPriority {
        public AsyncTask task;
        public int priority;
    }
    
}
