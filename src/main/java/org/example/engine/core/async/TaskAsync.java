package org.example.engine.core.async;

import org.example.engine.core.collections.Array;

// TODO: make abstract
public class TaskAsync {


    private volatile boolean complete = false;
    public Array<TaskAsync> prerequisites;
    String id;
    int counter = 0;
    int length;

    public TaskAsync(String id, int length, TaskAsync... prerequisites) {
        this.id = id;
        this.prerequisites = new Array<>();
        this.prerequisites.addAll(prerequisites);
        this.length = length;
    }

    public void task() {
        for (int i = 0; i < length; i++) {
            System.out.println(id + ": " + i);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            counter = i;
        }
    }

    // should only be called from the main Thread.
    public void onComplete() {

    }

}
