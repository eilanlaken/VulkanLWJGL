package org.example.engine.core.async;

import org.example.engine.core.collections.Array;

public abstract class Task {

    public Array<Task> prerequisites;
    private volatile boolean inProgress = false;
    private volatile boolean runComplete = false;

    public Task(Task... prerequisites) {
        this.prerequisites = new Array<>();
        this.prerequisites.addAll(prerequisites);
    }

    protected final void task() {
        synchronized (this) { if (runComplete) return; inProgress = true; }
        onStart();
        run();
        onComplete();
        synchronized (this) { inProgress = false; runComplete = true; }
    }

    public void onStart() {}
    public abstract void run();
    public void onComplete() {}

    public boolean isRunComplete() {
        return runComplete;
    }

}
