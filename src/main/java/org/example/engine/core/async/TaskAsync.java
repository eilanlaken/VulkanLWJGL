package org.example.engine.core.async;

import org.example.engine.core.collections.Array;

public abstract class TaskAsync {

    public Array<TaskAsync> prerequisites;
    private volatile boolean inProgress = false;
    private volatile boolean finished = false;

    public TaskAsync(TaskAsync... prerequisites) {
        this.prerequisites = new Array<>();
        this.prerequisites.addAll(prerequisites);
    }

    protected final void start() {
        synchronized (this) {
            if (finished) return;
            inProgress = true;
        }
        task();
        onComplete();
        synchronized (this) { inProgress = false; finished = true; }
    }

    public abstract void task();

    public void onComplete() {

    }

}
