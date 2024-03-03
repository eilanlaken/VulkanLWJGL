package org.example.engine.core.async;

import java.util.*;

public class TaskRunner {

    public static void runAsync(Task ...tasks) {
        for (Task task : tasks) runAsync(task);
    }

    public static void runAsync(Task task) {
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

    private static Thread start(Task task) {
        List<Thread> preRequisiteThreads = new ArrayList<>();
        for (Task prerequisite : task.prerequisites) {
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

        Thread taskThread = new Thread(task::task);
        taskThread.setDaemon(true);
        taskThread.start();
        return taskThread;
    }

    public static void runSync(Task ...tasks) {
        TaskPriority[] taskPriorities = new TaskPriority[tasks.length];
        for (int i = 0; i < taskPriorities.length; i++) {
            taskPriorities[i] = new TaskPriority();
            taskPriorities[i].task = tasks[i];
            taskPriorities[i].priority = countPrerequisites(tasks[i]);
        }
        Arrays.sort(taskPriorities, Comparator.comparingInt(p -> p.priority));
        for (TaskPriority taskPriority : taskPriorities) taskPriority.task.task();
    }


    private static int countPrerequisites(Task task) {
        Set<Task> allDistinctPrerequisites = new HashSet<>();
        collectDistinctPrerequisites(task, allDistinctPrerequisites);
        return allDistinctPrerequisites.size();
    }

    private static void collectDistinctPrerequisites(Task task, Set<Task> allDistinctPrerequisites) {
        if (task == null) return;
        for (Task prerequisite : task.prerequisites) {
            allDistinctPrerequisites.add(prerequisite);
            collectDistinctPrerequisites(prerequisite, allDistinctPrerequisites);
        }
    }

    private static class TaskPriority {
        public Task task;
        public int priority;
    }
    
}
