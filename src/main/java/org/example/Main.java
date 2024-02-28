package org.example;

import org.example.engine.core.application.Application;
import org.example.engine.core.graphics.WindowAttributes;
import org.example.game.WindowScreenTest1;

public class Main {

    public static void main(String[] args) throws Exception {
//        ApplicationConfig config = new ApplicationConfig();
//        Application.createSingleWindowApplication(config);
//        Application.launch(new WindowScreenTest_2());
        WindowAttributes config = new WindowAttributes();
        Application.createSingleWindowApplication(config);
        Application.launch(new WindowScreenTest1());
    }
}