package org.example.engine.core.async;

public class AsyncTaskRunner {

    public static void run(AsyncTask asyncTask) {

        AsyncTask[] dependencies = asyncTask.dependencies;
        if (dependencies != null && dependencies.length != 0) {

        }

    }

    public static void runAll(AsyncTask ...tasks) {
        if (tasks == null || tasks.length == 0) return;
        for (AsyncTask task : tasks) run(task);
    }

}
