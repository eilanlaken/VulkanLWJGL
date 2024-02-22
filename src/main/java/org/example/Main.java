package org.example;

import org.example.engine.core.application.Application;
import org.example.engine.core.application.ApplicationConfig;
import org.example.game.WindowScreenTest_1;
import org.example.game.WindowScreenTest_2;

public class Main {

    public static void main(String[] args) throws Exception {
        ApplicationConfig config = new ApplicationConfig();
        Application.createSingleWindowApplication(config);
        Application.launch(new WindowScreenTest_2());
    }
}