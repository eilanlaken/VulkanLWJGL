package org.example.engine.core.async;

public abstract class AsyncTask {

    // the array of Task(s) required to be completed BEFORE this Task.
    public AsyncTask[] dependencies;

    public AsyncTask(AsyncTask...dependencies) {
        this.dependencies = dependencies;
    }

    public abstract void task();
    public abstract void onComplete();

}
