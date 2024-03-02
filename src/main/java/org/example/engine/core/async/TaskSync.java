package org.example.engine.core.async;

import org.example.engine.core.collections.Array;

public abstract class TaskSync {

    private boolean complete;
    public Array<TaskSync> prerequisites;

    public TaskSync(TaskSync... prerequisites) {
        this.complete = false;
        this.prerequisites = new Array<>();
        this.prerequisites.addAll(prerequisites);
    }

    public abstract void run();

    public final synchronized void finish() {
        if (complete) return;
        run();
        this.complete = true;
    }

}
