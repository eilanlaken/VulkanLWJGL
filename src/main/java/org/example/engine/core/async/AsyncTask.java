package org.example.engine.core.async;

import org.example.engine.core.collections.CollectionsArray;

// TODO: test
public abstract class AsyncTask {

    protected CollectionsArray<AsyncTask> prerequisites;
    private volatile boolean inProgress = false;
    private volatile boolean complete = false;

    public AsyncTask(AsyncTask... prerequisites) {
        this.prerequisites = new CollectionsArray<>();
        this.prerequisites.addAll(prerequisites);
    }

    protected final void run() {
        synchronized (this) { if (complete) return; inProgress = true; }
        onStart();
        task();
        onComplete();
        synchronized (this) { inProgress = false; complete = true; }
    }

    public void onStart() {}
    public abstract void task();
    public void onComplete() {}

    public boolean isComplete() {
        return complete;
    }
    public boolean isInProgress() { return inProgress; }
}
