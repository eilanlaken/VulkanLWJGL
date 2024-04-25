package org.example.engine.core.async;

import org.example.engine.core.collections.CollectionsArray;

public abstract class AsyncTask {

    protected CollectionsArray<AsyncTask> prerequisites = new CollectionsArray<>();
    private volatile boolean              inProgress    = false;
    private volatile boolean              complete      = false;

    public AsyncTask(AsyncTask... prerequisites) {
        for (final AsyncTask task : prerequisites) {
            addPrerequisite(task);
        }
    }

    public final void addPrerequisite(AsyncTask task) {
        if (task == null) return;
        if (inProgress) throw new IllegalStateException("Cannot add prerequisite tasks to " + this.getClass().getSimpleName() + " while in progress.");
        if (complete)   throw new IllegalStateException("Cannot add prerequisite tasks to " + this.getClass().getSimpleName() + " after completed.");
        if (task == this) throw new IllegalArgumentException("A " + AsyncTask.class.getSimpleName() + " cannot be its own prerequisite.");
        if (task.isPrerequisite(this)) throw new IllegalArgumentException("Cyclic dependency: this " + this.getClass().getSimpleName() + " is a prerequisite of task: " + task + ". Cannot set task: " + task + " to be a prerequisite of " + this + ".");
        this.prerequisites.add(task);
    }

    public boolean isPrerequisite(AsyncTask task) {
        if (prerequisites.isEmpty()) return false;
        boolean found = false;
        for (AsyncTask prerequisite : prerequisites) {
            found |= (task == prerequisite) || prerequisite.isPrerequisite(task);
        }
        return found;
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
