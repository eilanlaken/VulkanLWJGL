package org.example.engine.core.async;

import org.example.engine.core.collections.Array;

public class AsyncTask {


    private volatile boolean complete = false;
    public Array<AsyncTask> prerequisites;
    String id;
    int counter = 0;
    int length;

    public AsyncTask(String id, int length, AsyncTask... prerequisites) {
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
