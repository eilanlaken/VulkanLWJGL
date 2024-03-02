package org.example;

import org.example.engine.core.application.Application;
import org.example.engine.core.async.AsyncTask;
import org.example.engine.core.async.AsyncTaskRunner;
import org.example.engine.core.graphics.WindowAttributes;
import org.example.game.WindowScreenTest_AssetStore_1;

public class Main {

    public static void main(String[] args) {
        testCode();

        WindowAttributes config = new WindowAttributes();
        Application.createSingleWindowApplication(config);
        Application.launch(new WindowScreenTest_AssetStore_1());
    }

    private static void testCode() {

        AsyncTask asyncTaskChildAAA = new AsyncTask("child AAA", 5);

        AsyncTask asyncTaskChildAA = new AsyncTask("child AA", 5, asyncTaskChildAAA);

        AsyncTask asyncTaskChildA = new AsyncTask("child A", 3, asyncTaskChildAA);
        AsyncTask asyncTaskChildB = new AsyncTask("child B", 6);
        AsyncTask parent = new AsyncTask("parent", 2, asyncTaskChildB, asyncTaskChildA);

        AsyncTaskRunner.submit(parent);
    }
}