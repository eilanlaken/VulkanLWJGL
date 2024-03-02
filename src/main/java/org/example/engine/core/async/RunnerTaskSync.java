package org.example.engine.core.async;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class RunnerTaskSync {

    public static void run(TaskSync ...tasks) {
        TaskPriority[] taskPriorities = new TaskPriority[tasks.length];
        for (int i = 0; i < taskPriorities.length; i++) {
            taskPriorities[i] = new TaskPriority();
            taskPriorities[i].task = tasks[i];
            taskPriorities[i].priority = countPrerequisites(tasks[i]);
        }
        Arrays.sort(taskPriorities, Comparator.comparingInt(p -> p.priority));
        for (TaskPriority taskPriority : taskPriorities) taskPriority.task.run();
    }


    public static int countPrerequisites(TaskSync task) {
        Set<TaskSync> allDistinctPrerequisites = new HashSet<>();
        collectDistinctPrerequisites(task, allDistinctPrerequisites);
        return allDistinctPrerequisites.size();
    }

    private static void collectDistinctPrerequisites(TaskSync task, Set<TaskSync> allDistinctPrerequisites) {
        if (task == null) return;
        for (TaskSync prerequisite : task.prerequisites) {
            allDistinctPrerequisites.add(prerequisite);
            collectDistinctPrerequisites(prerequisite, allDistinctPrerequisites);
        }
    }

    private static class TaskPriority {

        public TaskSync task;
        public int priority;

    }

}
